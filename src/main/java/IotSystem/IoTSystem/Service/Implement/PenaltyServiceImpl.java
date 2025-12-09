package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import IotSystem.IoTSystem.Model.Mappers.PenaltyMapper;
import IotSystem.IoTSystem.Model.Request.PenaltyRequest;
import IotSystem.IoTSystem.Model.Response.PenaltyResponse;
import IotSystem.IoTSystem.Model.Response.TransactionHistoryResponse;
import IotSystem.IoTSystem.Repository.*;
import IotSystem.IoTSystem.Service.IPenaltyService;
import IotSystem.IoTSystem.Service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PenaltyServiceImpl implements IPenaltyService {

    @Autowired
    private PenaltyRepository penaltyRepository;

    @Autowired
    private PenaltyPoliciesRepository penaltyPoliciesRepository;

    @Autowired
    private PenaltyDetailRepository penaltyDetailRepository;

    @Autowired
    private BorrowingRequestRepository borrowingRequestRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private WebSocketService webSocketService;


    @Override
    public List<PenaltyResponse> getPenaltyByAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Penalty> penalties = penaltyRepository.findPenaltiesByAccountId(account.getId());

        return penalties.stream().map(PenaltyMapper::toResponse).toList();
    }


    @Override
    public Penalty getById(UUID id) {
        return null;
    }

    @Override
    public List<PenaltyResponse> getAll(boolean isResolved) {
        List<Penalty> penalties = penaltyRepository.findByResolved(isResolved);
        return penalties.stream().map(PenaltyMapper::toResponse).toList();
    }

    @Override
    public PenaltyResponse create(PenaltyRequest request) {
        Penalty penalty = new Penalty();

        Account account = accountRepository.findById(request.getAccountId()).orElseThrow(() ->
                new ResourceNotFoundException("Did not found the Student/Lecturer: " + request.getAccountId()));

        BorrowingRequest borrowingRequest = borrowingRequestRepository.findById(request.getBorrowRequestId()).orElseThrow(() ->
                new ResourceNotFoundException("Did not found the Student/Lecturer: " + request.getBorrowRequestId()));

        penalty.setAccount(account);
        penalty.setRequest(borrowingRequest);
        penalty.setTake_effect_date(request.getTakeEffectDate());
        penalty.setResolved(request.isResolved());
        penalty.setNote(request.getNote());
        penalty.setSemester(request.getSemester());
        penalty.setKit_type(request.getKitType());
        penalty.setTotal_ammount(request.getTotalAmount());

        penalty = penaltyRepository.save(penalty);

        // Send WebSocket update to user
        try {
            PenaltyResponse penaltyResponse = PenaltyMapper.toResponse(penalty);
            webSocketService.sendPenaltyUpdateToUser(account.getId().toString(), penaltyResponse);
        } catch (Exception e) {
            System.err.println("Error sending WebSocket penalty update: " + e.getMessage());
        }

        // Rental fee is handled in penalty payment logic, not as a separate penalty detail
        // No need to create penalty detail for rental fee here

        return PenaltyMapper.toResponse(penalty);
    }

    @Override
    public Penalty update(UUID id, Penalty penalty) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    @Transactional
    public void confirmPaymentForPenalty(UUID penaltyId) {
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new ResourceNotFoundException("Penalty not found"));
        Account account = penalty.getAccount();
        Wallet wallet = account.getWallet();
        if(wallet == null) throw new RuntimeException("Wallet not found for this account!");

        java.math.BigDecimal penaltyAmount = new java.math.BigDecimal(penalty.getTotal_ammount().toString());
        java.math.BigDecimal depositAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal refundAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal amountToPayFromWallet = penaltyAmount;

        // Get deposit amount from borrowing request if available
        BorrowingRequest borrowingRequest = penalty.getRequest();
        if (borrowingRequest != null && borrowingRequest.getDepositAmount() != null) {
            java.math.BigDecimal originalDepositAmount = new java.math.BigDecimal(borrowingRequest.getDepositAmount().toString());
            final UUID borrowingRequestId = borrowingRequest.getId();

            // IMPORTANT: The correct logic is:
            // 1. If admin submits inspection WITH penalty → penalty is created FIRST, then status = RETURNED
            // 2. When status = RETURNED, backend checks for unresolved penalty → finds penalty → does NOT refund deposit
            // 3. Therefore, if this penalty exists and is being paid, deposit has NOT been refunded yet
            // 4. We should use deposit to pay penalty, and refund the difference if penalty < deposit

            // Check if deposit was refunded by looking for "kit returned without penalty" transaction
            // This only happens when kit was returned WITHOUT any penalty existing at that time
            final java.math.BigDecimal depositAmountForCheck = originalDepositAmount;
            boolean depositAlreadyFullyRefunded = false;

            // Only consider deposit refunded if:
            // 1. There's a REFUND transaction with "kit returned without penalty" for this borrowing request
            // 2. AND this penalty was created AFTER the refund transaction (meaning penalty was created later)
            WalletTransaction refundTx = walletTransactionRepository.findAll().stream()
                    .filter(tx -> tx.getWallet() != null &&
                            tx.getWallet().getId().equals(wallet.getId()) &&
                            tx.getTransactionType() == IotSystem.IoTSystem.Model.Entities.Enum.Wallet_Transaction_Type.REFUND &&
                            tx.getDescription() != null &&
                            tx.getDescription().contains("kit returned without penalty") &&
                            tx.getDescription().contains("Borrowing Request: " + borrowingRequestId.toString()) &&
                            Math.abs(tx.getAmount() - depositAmountForCheck.doubleValue()) < 0.01)
                    .findFirst()
                    .orElse(null);

            if (refundTx != null) {
                // Found refund transaction - check if this penalty was created AFTER the refund
                // If penalty was created AFTER refund, it means deposit was refunded first, then penalty created later
                java.time.LocalDateTime penaltyCreatedAt = penalty.getCreatedAt();
                java.time.LocalDateTime refundCreatedAt = refundTx.getCreatedAt();

                if (penaltyCreatedAt != null && refundCreatedAt != null) {
                    // If penalty was created AFTER refund, deposit was already refunded
                    if (penaltyCreatedAt.isAfter(refundCreatedAt)) {
                        depositAlreadyFullyRefunded = true;
                        System.out.println("Deposit was refunded at " + refundCreatedAt + ", but penalty was created later at " + penaltyCreatedAt +
                                ". Deposit already refunded, user must pay full penalty from wallet.");
                    } else {
                        // Penalty was created before or at the same time as refund
                        // This shouldn't happen normally, but if it does, ignore the refund transaction
                        // because penalty existed when kit was returned, so deposit should NOT have been refunded
                        System.out.println("Penalty was created at " + penaltyCreatedAt + ", refund transaction at " + refundCreatedAt +
                                ". This indicates penalty existed when kit was returned, so deposit should NOT have been refunded. Ignoring refund transaction.");
                        depositAlreadyFullyRefunded = false;
                    }
                } else {
                    // Can't compare timestamps, be conservative: assume deposit was NOT refunded
                    // (because if penalty exists, deposit should not have been refunded)
                    System.out.println("Cannot compare timestamps. Assuming deposit was NOT refunded because penalty exists.");
                    depositAlreadyFullyRefunded = false;
                }
            }

            if (depositAlreadyFullyRefunded) {
                // Deposit was already fully refunded (no penalty was created, kit returned clean)
                // Now penalty was created later, so user must pay full penalty from wallet
                System.out.println("Deposit already fully refunded for borrowing request: " + borrowingRequest.getId() + ". User must pay full penalty from wallet.");
                depositAmount = java.math.BigDecimal.ZERO;
                refundAmount = java.math.BigDecimal.ZERO;
                amountToPayFromWallet = penaltyAmount;
            } else {
                // Deposit has NOT been refunded yet (penalty was created when kit was returned)
                // Use the original deposit amount to calculate refund/payment
                depositAmount = originalDepositAmount;

                System.out.println("Deposit has NOT been refunded yet for borrowing request: " + borrowingRequest.getId() +
                        ". Deposit amount: " + depositAmount + " VND. Penalty amount: " + penaltyAmount + " VND");

                // Calculate refund and amount to pay from wallet
                if (penaltyAmount.compareTo(depositAmount) < 0) {
                    // Penalty < Deposit: Refund the difference (deposit - penalty)
                    refundAmount = depositAmount.subtract(penaltyAmount);
                    amountToPayFromWallet = java.math.BigDecimal.ZERO;
                    System.out.println("Penalty (" + penaltyAmount + ") < Deposit (" + depositAmount + "). Refunding difference: " + refundAmount);
                } else {
                    // Penalty >= Deposit: No refund, deduct deposit and pay remaining from wallet
                    refundAmount = java.math.BigDecimal.ZERO;
                    amountToPayFromWallet = penaltyAmount.subtract(depositAmount);
                    System.out.println("Penalty (" + penaltyAmount + ") >= Deposit (" + depositAmount + "). No refund. Paying from wallet: " + amountToPayFromWallet);
                }
            }
        }

        // Check if wallet has enough balance for the amount to pay from wallet
        if (amountToPayFromWallet.compareTo(java.math.BigDecimal.ZERO) > 0) {
            if (wallet.getBalance().compareTo(amountToPayFromWallet) < 0) {
                throw new RuntimeException("Số dư ví không đủ. Vui lòng nạp thêm tiền! Cần: " + amountToPayFromWallet + " VND");
            }
        }

        // Process refund if any
        WalletTransaction savedRefundTransaction = null;
        if (refundAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
            // Add refund to wallet
            wallet.setBalance(wallet.getBalance().add(refundAmount));

            // Create refund transaction
            WalletTransaction refundTransaction = new WalletTransaction();
            refundTransaction.setWallet(wallet);
            refundTransaction.setAmount(refundAmount.doubleValue());
            refundTransaction.setTransactionType(IotSystem.IoTSystem.Model.Entities.Enum.Wallet_Transaction_Type.REFUND);
            refundTransaction.setTransactionStatus(IotSystem.IoTSystem.Model.Entities.Enum.Status.Wallet_Transaction_Status.COMPLETED);
            refundTransaction.setDescription("Refund from rental deposit after penalty payment. Penalty: " + penaltyId + " (Deposit: " + depositAmount + " VND, Penalty: " + penaltyAmount + " VND, Refund: " + refundAmount + " VND)");
            refundTransaction.setPaymentMethod("Wallet");
            refundTransaction.setCreatedAt(java.time.LocalDateTime.now());
            refundTransaction.setUpdatedAt(java.time.LocalDateTime.now());
            savedRefundTransaction = walletTransactionRepository.save(refundTransaction);

            System.out.println("Refund processed: " + refundAmount + " VND added to wallet. New balance: " + wallet.getBalance());
        }

        // Process payment from wallet if needed
        WalletTransaction savedPaymentTransaction = null;
        WalletTransaction savedDepositUsageTransaction = null;
        if (amountToPayFromWallet.compareTo(java.math.BigDecimal.ZERO) > 0) {
            // Deduct from wallet and create transaction
            WalletTransaction paymentTransaction = new WalletTransaction();
            paymentTransaction.setWallet(wallet);
            paymentTransaction.setAmount(amountToPayFromWallet.doubleValue());
            paymentTransaction.setTransactionType(IotSystem.IoTSystem.Model.Entities.Enum.Wallet_Transaction_Type.PENALTY_PAYMENT);
            paymentTransaction.setTransactionStatus(IotSystem.IoTSystem.Model.Entities.Enum.Status.Wallet_Transaction_Status.COMPLETED);
            paymentTransaction.setDescription("Payment for Penalty: " + penaltyId + (depositAmount.compareTo(java.math.BigDecimal.ZERO) > 0 ? " (after deducting deposit: " + depositAmount + " VND)" : ""));
            paymentTransaction.setPaymentMethod("Wallet");
            paymentTransaction.setCreatedAt(java.time.LocalDateTime.now());
            paymentTransaction.setUpdatedAt(java.time.LocalDateTime.now());
            savedPaymentTransaction = walletTransactionRepository.save(paymentTransaction);

            // Update wallet balance
            wallet.setBalance(wallet.getBalance().subtract(amountToPayFromWallet));

            System.out.println("Payment processed: " + amountToPayFromWallet + " VND deducted from wallet. New balance: " + wallet.getBalance());
        } else if (depositAmount.compareTo(java.math.BigDecimal.ZERO) > 0 && penaltyAmount.compareTo(depositAmount) <= 0) {
            // When penalty <= deposit, create a transaction to record that deposit was used to pay penalty
            // This helps with tracking and transparency
            WalletTransaction depositUsageTransaction = new WalletTransaction();
            depositUsageTransaction.setWallet(wallet);
            depositUsageTransaction.setAmount(penaltyAmount.doubleValue());
            depositUsageTransaction.setTransactionType(IotSystem.IoTSystem.Model.Entities.Enum.Wallet_Transaction_Type.PENALTY_PAYMENT);
            depositUsageTransaction.setTransactionStatus(IotSystem.IoTSystem.Model.Entities.Enum.Status.Wallet_Transaction_Status.COMPLETED);
            depositUsageTransaction.setDescription("Penalty paid using deposit. Penalty: " + penaltyId + " (Deposit used: " + penaltyAmount + " VND, Refunded: " + refundAmount + " VND)");
            depositUsageTransaction.setPaymentMethod("Deposit");
            depositUsageTransaction.setCreatedAt(java.time.LocalDateTime.now());
            depositUsageTransaction.setUpdatedAt(java.time.LocalDateTime.now());
            savedDepositUsageTransaction = walletTransactionRepository.save(depositUsageTransaction);

            System.out.println("Penalty paid using deposit: " + penaltyAmount + " VND. Deposit was already deducted when request was created.");
        }

        // Save wallet with updated balance
        wallet.setUpdatedAt(java.time.LocalDateTime.now());
        walletRepository.save(wallet);

        System.out.println("Final wallet balance: " + wallet.getBalance() + " VND");

        // Send WebSocket update to user for wallet balance and transactions
        try {
            java.math.BigDecimal finalBalance = wallet.getBalance();
            java.util.List<TransactionHistoryResponse> transactions = new java.util.ArrayList<>();

            // Add refund transaction if any (use saved transaction directly)
            if (savedRefundTransaction != null) {
                TransactionHistoryResponse refundResponse = new TransactionHistoryResponse();
                refundResponse.setId(savedRefundTransaction.getId());
                refundResponse.setType(savedRefundTransaction.getTransactionType().name());
                refundResponse.setAmount(savedRefundTransaction.getAmount());
                refundResponse.setDescription(savedRefundTransaction.getDescription());
                refundResponse.setStatus(savedRefundTransaction.getTransactionStatus().name());
                refundResponse.setCreatedAt(savedRefundTransaction.getCreatedAt());
                refundResponse.setUpdatedAt(savedRefundTransaction.getUpdatedAt());
                transactions.add(refundResponse);
                System.out.println("Added refund transaction to WebSocket update: " + refundAmount + " VND");
            }

            // Add payment transaction if any (from wallet - use saved transaction directly)
            if (savedPaymentTransaction != null) {
                TransactionHistoryResponse paymentResponse = new TransactionHistoryResponse();
                paymentResponse.setId(savedPaymentTransaction.getId());
                paymentResponse.setType(savedPaymentTransaction.getTransactionType().name());
                paymentResponse.setAmount(savedPaymentTransaction.getAmount());
                paymentResponse.setDescription(savedPaymentTransaction.getDescription());
                paymentResponse.setStatus(savedPaymentTransaction.getTransactionStatus().name());
                paymentResponse.setCreatedAt(savedPaymentTransaction.getCreatedAt());
                paymentResponse.setUpdatedAt(savedPaymentTransaction.getUpdatedAt());
                transactions.add(paymentResponse);
                System.out.println("Added payment transaction to WebSocket update: " + amountToPayFromWallet + " VND");
            }

            // Add deposit usage transaction if penalty was paid using deposit (use saved transaction directly)
            if (savedDepositUsageTransaction != null) {
                TransactionHistoryResponse depositResponse = new TransactionHistoryResponse();
                depositResponse.setId(savedDepositUsageTransaction.getId());
                depositResponse.setType(savedDepositUsageTransaction.getTransactionType().name());
                depositResponse.setAmount(savedDepositUsageTransaction.getAmount());
                depositResponse.setDescription(savedDepositUsageTransaction.getDescription());
                depositResponse.setStatus(savedDepositUsageTransaction.getTransactionStatus().name());
                depositResponse.setCreatedAt(savedDepositUsageTransaction.getCreatedAt());
                depositResponse.setUpdatedAt(savedDepositUsageTransaction.getUpdatedAt());
                transactions.add(depositResponse);
                System.out.println("Added deposit usage transaction to WebSocket update: " + penaltyAmount + " VND");
            }

            // Send wallet update with new balance
            java.util.Map<String, Object> walletUpdate = new java.util.HashMap<>();
            walletUpdate.put("balance", finalBalance.doubleValue());
            if (!transactions.isEmpty()) {
                walletUpdate.put("transaction", transactions.get(transactions.size() - 1)); // Send the last transaction
            }

            webSocketService.sendWalletUpdateToUser(account.getId().toString(), walletUpdate);

            // Send each transaction separately
            for (TransactionHistoryResponse txResponse : transactions) {
                webSocketService.sendWalletTransactionToUser(account.getId().toString(), txResponse);
            }

            System.out.println("WebSocket wallet update sent to user: " + account.getId() + ", new balance: " + finalBalance);
        } catch (Exception e) {
            System.err.println("Error sending WebSocket wallet update: " + e.getMessage());
            e.printStackTrace();
            // Continue even if WebSocket fails
        }

        // Update penalty
        penalty.setResolved(true);
        penaltyRepository.save(penalty);
    }
}
