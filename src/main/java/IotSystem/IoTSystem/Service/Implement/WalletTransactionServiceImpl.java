package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Model.Entities.Enum.NotificationSubType;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.Wallet_Transaction_Status;
import IotSystem.IoTSystem.Model.Entities.Enum.Wallet_Transaction_Type;
import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import IotSystem.IoTSystem.Model.Request.NotificationRequest;
import IotSystem.IoTSystem.Model.Response.TransactionHistoryResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.BorrowingRequestRepository;
import IotSystem.IoTSystem.Repository.PenaltyRepository;
import IotSystem.IoTSystem.Repository.WalletRepository;
import IotSystem.IoTSystem.Repository.WalletTransactionRepository;
import IotSystem.IoTSystem.Service.INotificationService;
import IotSystem.IoTSystem.Service.IWalletTransactionService;
import IotSystem.IoTSystem.Service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WalletTransactionServiceImpl implements IWalletTransactionService {
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private PenaltyRepository penaltyRepository;

    @Autowired
    private BorrowingRequestRepository borrowingRequestRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private INotificationService notificationService;

    @Override
    public List<TransactionHistoryResponse> getAll() {
        List<WalletTransaction> transactions = walletTransactionRepository.findAllExceptTopUp();
        List<TransactionHistoryResponse> responses = new ArrayList<>();

        for (WalletTransaction transaction : transactions) {
            TransactionHistoryResponse response = new TransactionHistoryResponse();

            response.setId(transaction.getId());
            response.setType(transaction.getTransactionType() != null ? transaction.getTransactionType().name() : null);
            response.setAmount(transaction.getAmount());
            response.setPreviousBalance(transaction.getPreviousBalance());
            response.setDescription(transaction.getDescription());
            response.setStatus(transaction.getTransactionStatus() != null ? transaction.getTransactionStatus().name() : null);
            response.setCreatedAt(transaction.getCreatedAt());
            response.setUpdatedAt(transaction.getUpdatedAt());

            // Get wallet and account information
            Wallet wallet = transaction.getWallet();
            if (wallet != null) {
                Account account = wallet.getAccount();
                if (account != null) {
                    response.setEmail(account.getEmail());
                    response.setUserName(account.getFullName());
                }
            }

            if (transaction.getTransactionType() == Wallet_Transaction_Type.PENALTY_PAYMENT) {
                try {
                    Wallet walletRef = transaction.getWallet();
                    if (walletRef != null && walletRef.getAccount() != null) {
                        List<Penalty> penalties = penaltyRepository.findAll().stream()
                                .filter(p -> p.getAccount() != null &&
                                        p.getAccount().getId().equals(walletRef.getAccount().getId()) &&
                                        p.getTotal_ammount() != null &&
                                        Math.abs(p.getTotal_ammount().doubleValue() - transaction.getAmount()) < 0.01)
                                .collect(Collectors.toList());

                        if (!penalties.isEmpty()) {
                            Penalty penalty = penalties.get(0);
                            response.setPenaltyId(penalty.getId());
                            response.setPenaltyNote(penalty.getNote());

                            if (penalty.getRequest() != null) {
                                BorrowingRequest borrowingRequest = penalty.getRequest();
                                response.setBorrowingRequestId(borrowingRequest.getId());
                                response.setDepositAmount(borrowingRequest.getDepositAmount());

                                if (borrowingRequest.getKit() != null) {
                                    response.setKitName(borrowingRequest.getKit().getKitName());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Log error but continue processing
                    System.err.println("Error fetching penalty information: " + e.getMessage());
                }
            } else if (transaction.getTransactionType() == Wallet_Transaction_Type.RENTAL_FEE ||
                    transaction.getTransactionType() == Wallet_Transaction_Type.REFUND) {
                // Find related borrowing request based on amount and description
                try {
                    Wallet walletRef = transaction.getWallet();
                    if (walletRef != null && walletRef.getAccount() != null) {
                        List<BorrowingRequest> borrowingRequests = borrowingRequestRepository.findAll().stream()
                                .filter(br -> br.getRequestedBy() != null &&
                                        br.getRequestedBy().getId().equals(walletRef.getAccount().getId()) &&
                                        br.getDepositAmount() != null &&
                                        Math.abs(br.getDepositAmount() - transaction.getAmount()) < 0.01)
                                .collect(Collectors.toList());

                        if (!borrowingRequests.isEmpty()) {
                            // Get the most recent borrowing request matching the amount
                            BorrowingRequest borrowingRequest = borrowingRequests.stream()
                                    .sorted((a, b) -> {
                                        if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                                        if (a.getCreatedAt() == null) return 1;
                                        if (b.getCreatedAt() == null) return -1;
                                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                                    })
                                    .findFirst()
                                    .orElse(null);

                            if (borrowingRequest != null) {
                                response.setBorrowingRequestId(borrowingRequest.getId());
                                response.setDepositAmount(borrowingRequest.getDepositAmount());

                                if (borrowingRequest.getKit() != null) {
                                    response.setKitName(borrowingRequest.getKit().getKitName());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Log error but continue processing
                    System.err.println("Error fetching borrowing request information: " + e.getMessage());
                }
            }

            responses.add(response);
        }
        responses.sort((a, b) -> {
            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });

        return responses;
    }

    @Override
    @Transactional
    public WalletTransaction createTopUp(Double amount, String description) {
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get wallet
        Wallet wallet = account.getWallet();
        if (wallet == null) {
            throw new RuntimeException("Wallet not found");
        }

        // Get current balance before transaction
        BigDecimal currentBalance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
        Double previousBalance = currentBalance.doubleValue();

        // Create transaction
        WalletTransaction transaction = new WalletTransaction();
        transaction.setAmount(amount);
        transaction.setPreviousBalance(previousBalance);
        transaction.setTransactionType(Wallet_Transaction_Type.TOP_UP);
        transaction.setTransactionStatus(Wallet_Transaction_Status.COMPLETED);
        transaction.setDescription(description != null ? description : "Top-up wallet");
        transaction.setPaymentMethod("Direct");
        transaction.setWallet(wallet);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());

        transaction = walletTransactionRepository.save(transaction);

        // Update wallet balance
        BigDecimal newBalance = currentBalance.add(BigDecimal.valueOf(amount));
        wallet.setBalance(newBalance);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Send WebSocket update to user
        try {
            TransactionHistoryResponse transactionResponse = new TransactionHistoryResponse();
            transactionResponse.setId(transaction.getId());
            transactionResponse.setType(transaction.getTransactionType().name());
            transactionResponse.setAmount(transaction.getAmount());
            transactionResponse.setPreviousBalance(transaction.getPreviousBalance());
            transactionResponse.setDescription(transaction.getDescription());
            transactionResponse.setStatus(transaction.getTransactionStatus().name());
            transactionResponse.setCreatedAt(transaction.getCreatedAt());
            transactionResponse.setUpdatedAt(transaction.getUpdatedAt());

            // Send wallet update with new balance
            java.util.Map<String, Object> walletUpdate = new java.util.HashMap<>();
            walletUpdate.put("balance", newBalance.doubleValue());
            walletUpdate.put("transaction", transactionResponse);

            webSocketService.sendWalletTransactionToUser(account.getId().toString(), transactionResponse);
            webSocketService.sendSystemUpdate("TRANSACTION", "UPDATE");
        } catch (Exception e) {
            System.err.println("Error sending WebSocket update: " + e.getMessage());
        }

        return transaction;
    }

    @Override
    @Transactional
    public WalletTransaction transfer(String recipientEmail, Double amount, String description) {
        // Get current user (sender)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = auth.getName();
        Account senderAccount = accountRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate amount
        if (amount == null || amount < 10000) {
            throw new RuntimeException("Amount must be at least 10,000 VND");
        }

        // Validate recipient email exists
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            throw new RuntimeException("Recipient email is required");
        }

        if (!accountRepository.existsByEmail(recipientEmail.trim())) {
            throw new RuntimeException("Recipient email does not exist in the system");
        }

        // Get sender wallet
        Wallet senderWallet = senderAccount.getWallet();
        if (senderWallet == null) {
            throw new RuntimeException("Sender wallet not found");
        }

        // Check if sender has sufficient balance
        BigDecimal senderBalance = senderWallet.getBalance() != null ? senderWallet.getBalance() : BigDecimal.ZERO;
        if (senderBalance.compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new RuntimeException("Insufficient balance. Current balance: " + senderBalance.doubleValue() + " VND");
        }

        // Get recipient account
        Account recipientAccount = accountRepository.findByEmail(recipientEmail.trim())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        // Check if recipient is the same as sender
        if (recipientAccount.getId().equals(senderAccount.getId())) {
            throw new RuntimeException("Cannot transfer to yourself");
        }

        // Get recipient wallet
        Wallet recipientWallet = recipientAccount.getWallet();
        if (recipientWallet == null) {
            throw new RuntimeException("Recipient wallet not found");
        }

        // Get current balance before transaction for sender
        Double senderPreviousBalance = senderBalance.doubleValue();

        // Create transaction for sender (outgoing transfer - negative amount)
        WalletTransaction senderTransaction = new WalletTransaction();
        senderTransaction.setAmount(-amount); // Negative amount for sender
        senderTransaction.setPreviousBalance(senderPreviousBalance);
        senderTransaction.setTransactionType(Wallet_Transaction_Type.TRANSFER);
        senderTransaction.setTransactionStatus(Wallet_Transaction_Status.COMPLETED);
        senderTransaction.setDescription(description != null ?
                description + " (To: " + recipientEmail + ")" :
                "Transfer to " + recipientEmail);
        senderTransaction.setPaymentMethod("Wallet Transfer");
        senderTransaction.setWallet(senderWallet);
        senderTransaction.setCreatedAt(LocalDateTime.now());
        senderTransaction.setUpdatedAt(LocalDateTime.now());

        senderTransaction = walletTransactionRepository.save(senderTransaction);

        // Update sender wallet balance
        BigDecimal newSenderBalance = senderBalance.subtract(BigDecimal.valueOf(amount));
        senderWallet.setBalance(newSenderBalance);
        senderWallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(senderWallet);

        // Get current balance before transaction for recipient
        BigDecimal recipientBalance = recipientWallet.getBalance() != null ? recipientWallet.getBalance() : BigDecimal.ZERO;
        Double recipientPreviousBalance = recipientBalance.doubleValue();

        // Create transaction for recipient (incoming transfer - positive amount)
        WalletTransaction recipientTransaction = new WalletTransaction();
        recipientTransaction.setAmount(amount); // Positive amount for recipient
        recipientTransaction.setPreviousBalance(recipientPreviousBalance);
        recipientTransaction.setTransactionType(Wallet_Transaction_Type.TRANSFER);
        recipientTransaction.setTransactionStatus(Wallet_Transaction_Status.COMPLETED);
        recipientTransaction.setDescription(description != null ?
                description + " (From: " + senderEmail + ")" :
                "Transfer from " + senderEmail);
        recipientTransaction.setPaymentMethod("Wallet Transfer");
        recipientTransaction.setWallet(recipientWallet);
        recipientTransaction.setCreatedAt(LocalDateTime.now());
        recipientTransaction.setUpdatedAt(LocalDateTime.now());

        recipientTransaction = walletTransactionRepository.save(recipientTransaction);

        // Update recipient wallet balance
        BigDecimal newRecipientBalance = recipientBalance.add(BigDecimal.valueOf(amount));
        recipientWallet.setBalance(newRecipientBalance);
        recipientWallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(recipientWallet);

        // Send WebSocket updates to sender
        try {
            TransactionHistoryResponse senderTransactionResponse = new TransactionHistoryResponse();
            senderTransactionResponse.setId(senderTransaction.getId());
            senderTransactionResponse.setType(senderTransaction.getTransactionType().name());
            senderTransactionResponse.setAmount(senderTransaction.getAmount());
            senderTransactionResponse.setPreviousBalance(senderTransaction.getPreviousBalance());
            senderTransactionResponse.setDescription(senderTransaction.getDescription());
            senderTransactionResponse.setStatus(senderTransaction.getTransactionStatus().name());
            senderTransactionResponse.setCreatedAt(senderTransaction.getCreatedAt());
            senderTransactionResponse.setUpdatedAt(senderTransaction.getUpdatedAt());

            java.util.Map<String, Object> senderWalletUpdate = new java.util.HashMap<>();
            senderWalletUpdate.put("balance", newSenderBalance.doubleValue());
            senderWalletUpdate.put("transaction", senderTransactionResponse);

            webSocketService.sendWalletUpdateToUser(senderAccount.getId().toString(), senderWalletUpdate);
            webSocketService.sendWalletTransactionToUser(senderAccount.getId().toString(), senderTransactionResponse);
        } catch (Exception e) {
            System.err.println("Error sending WebSocket update to sender: " + e.getMessage());
        }

        // Send WebSocket updates to recipient
        try {
            TransactionHistoryResponse recipientTransactionResponse = new TransactionHistoryResponse();
            recipientTransactionResponse.setId(recipientTransaction.getId());
            recipientTransactionResponse.setType(recipientTransaction.getTransactionType().name());
            recipientTransactionResponse.setAmount(recipientTransaction.getAmount());
            recipientTransactionResponse.setPreviousBalance(recipientTransaction.getPreviousBalance());
            recipientTransactionResponse.setDescription(recipientTransaction.getDescription());
            recipientTransactionResponse.setStatus(recipientTransaction.getTransactionStatus().name());
            recipientTransactionResponse.setCreatedAt(recipientTransaction.getCreatedAt());
            recipientTransactionResponse.setUpdatedAt(recipientTransaction.getUpdatedAt());

            java.util.Map<String, Object> recipientWalletUpdate = new java.util.HashMap<>();
            recipientWalletUpdate.put("balance", newRecipientBalance.doubleValue());
            recipientWalletUpdate.put("transaction", recipientTransactionResponse);

            webSocketService.sendWalletTransactionToUser(recipientAccount.getId().toString(), recipientTransactionResponse);
            webSocketService.sendSystemUpdate("TRANSACTION", "UPDATE");
        } catch (Exception e) {
            System.err.println("Error sending WebSocket update to recipient: " + e.getMessage());
        }

        // Create notification for sender
        try {
            NotificationRequest senderNotificationRequest = new NotificationRequest();
            senderNotificationRequest.setSubType(NotificationSubType.TRANSFER_SENT);
            senderNotificationRequest.setUserId(senderAccount.getId());
            String formattedAmount = String.format("%,.0f", amount);
            String senderMessage = "Bạn đã chuyển " + formattedAmount + " VND đến " + recipientEmail;
            if (description != null && !description.trim().isEmpty()) {
                senderMessage += " - " + description;
            }
            senderNotificationRequest.setMessage(senderMessage);
            notificationService.create(senderNotificationRequest);
        } catch (Exception e) {
            System.err.println("Error creating notification for sender: " + e.getMessage());
        }

        // Create notification for recipient
        try {
            NotificationRequest recipientNotificationRequest = new NotificationRequest();
            recipientNotificationRequest.setSubType(NotificationSubType.TRANSFER_RECEIVED);
            recipientNotificationRequest.setUserId(recipientAccount.getId());
            String formattedAmount = String.format("%,.0f", amount);
            String recipientMessage = "Bạn đã nhận " + formattedAmount + " VND từ " + senderEmail;
            if (description != null && !description.trim().isEmpty()) {
                recipientMessage += " - " + description;
            }
            recipientNotificationRequest.setMessage(recipientMessage);
            notificationService.create(recipientNotificationRequest);
        } catch (Exception e) {
            System.err.println("Error creating notification for recipient: " + e.getMessage());
        }

        // Return sender transaction as the main transaction
        return senderTransaction;
    }

    public void delete(UUID id) {
        walletTransactionRepository.deleteById(id);
    }

    @Override
    public List<TransactionHistoryResponse> getTransactionHistory() {
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TransactionHistoryResponse> history = new ArrayList<>();

        // Get wallet transactions
        Wallet wallet = account.getWallet();
        if (wallet != null) {
            List<WalletTransaction> walletTransactions = walletTransactionRepository.findTransactionsByWalletId(wallet.getId());

            for (WalletTransaction transaction : walletTransactions) {
                TransactionHistoryResponse response = new TransactionHistoryResponse();
                response.setId(transaction.getId());
                response.setType(transaction.getTransactionType().name());
                response.setAmount(transaction.getAmount());
                response.setPreviousBalance(transaction.getPreviousBalance());
                response.setDescription(transaction.getDescription());
                response.setStatus(transaction.getTransactionStatus().name());
                response.setCreatedAt(transaction.getCreatedAt());
                response.setUpdatedAt(transaction.getUpdatedAt());

                history.add(response);
            }
        }

        // Sort by createdAt descending (most recent first)
        history.sort((a, b) -> {
            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });

        return history;
    }
}
