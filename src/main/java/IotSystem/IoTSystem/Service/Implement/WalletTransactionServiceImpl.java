package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.Wallet_Transaction_Status;
import IotSystem.IoTSystem.Model.Entities.Enum.Wallet_Transaction_Type;
import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import IotSystem.IoTSystem.Model.Response.TransactionHistoryResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.BorrowingRequestRepository;
import IotSystem.IoTSystem.Repository.PenaltyRepository;
import IotSystem.IoTSystem.Repository.WalletRepository;
import IotSystem.IoTSystem.Repository.WalletTransactionRepository;
import IotSystem.IoTSystem.Service.IWalletTransactionService;
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

    @Override
    public List<TransactionHistoryResponse> getAll() {
        List<WalletTransaction> transactions = walletTransactionRepository.findAllExceptTopUp();
        List<TransactionHistoryResponse> responses = new ArrayList<>();

        for (WalletTransaction transaction : transactions) {
            TransactionHistoryResponse response = new TransactionHistoryResponse();

            response.setId(transaction.getId());
            response.setType(transaction.getTransactionType() != null ? transaction.getTransactionType().name() : null);
            response.setAmount(transaction.getAmount());
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

        // Create transaction
        WalletTransaction transaction = new WalletTransaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(Wallet_Transaction_Type.TOP_UP);
        transaction.setTransactionStatus(Wallet_Transaction_Status.COMPLETED);
        transaction.setDescription(description != null ? description : "Top-up wallet");
        transaction.setPaymentMethod("Direct");
        transaction.setWallet(wallet);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());

        transaction = walletTransactionRepository.save(transaction);

        // Update wallet balance
        BigDecimal currentBalance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
        BigDecimal newBalance = currentBalance.add(BigDecimal.valueOf(amount));
        wallet.setBalance(newBalance);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        return transaction;
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
