package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Model.Entities.PenaltyDetail;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import IotSystem.IoTSystem.Model.Mappers.PenaltyMapper;
import IotSystem.IoTSystem.Model.Request.PenaltyRequest;
import IotSystem.IoTSystem.Model.Response.PenaltyResponse;
import IotSystem.IoTSystem.Repository.*;
import IotSystem.IoTSystem.Service.IPenaltyService;
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

        penaltyRepository.save(penalty);

        // Auto-create a PenaltyDetail for rental 50% fee (depositAmount) if available
        try {
            Double depositAmount = borrowingRequest.getDepositAmount();
            if (depositAmount != null && depositAmount > 0) {
                PenaltyDetail rentalDetail = new PenaltyDetail();
                rentalDetail.setAmount(depositAmount);
                rentalDetail.setDescription("Trả tiền thuê kit (50%)");
                rentalDetail.setPenalty(penalty);
                // policies is null by design for rental fee detail
                // createdAt will be handled by Base/auditing if configured; otherwise set now
                // If PenaltyDetail has createdAt in Base, skip; else set if available
                try {
                    // reflectively set createdAt when field exists and is nullable
                    rentalDetail.setCreatedAt(java.time.LocalDateTime.now());
                } catch (Exception ignored) {}
                penaltyDetailRepository.save(rentalDetail);
            }
        } catch (Exception e) {
            // Do not block penalty creation if detail creation fails; log only
            System.err.println("[PenaltyService] Failed to create rental 50% PenaltyDetail: " + e.getMessage());
        }

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
        if (wallet.getBalance().compareTo(penaltyAmount) < 0) {
            throw new RuntimeException("Số dư ví không đủ. Vui lòng nạp thêm tiền!");
        }
        // Trừ tiền và tạo transaction
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setAmount(penaltyAmount.doubleValue());
        transaction.setTransactionType(IotSystem.IoTSystem.Model.Entities.Enum.Wallet_Transaction_Type.PENALTY_PAYMENT);
        transaction.setTransactionStatus(IotSystem.IoTSystem.Model.Entities.Enum.Status.Wallet_Transaction_Status.COMPLETED);
        transaction.setDescription("Payment for Penalty: " + penaltyId);
        transaction.setPaymentMethod("Wallet");
        transaction.setCreatedAt(java.time.LocalDateTime.now());
        walletTransactionRepository.save(transaction);
        // Update số dư
        wallet.setBalance(wallet.getBalance().subtract(penaltyAmount));
        walletRepository.save(wallet);
        // Update penalty
        penalty.setResolved(true);
        penaltyRepository.save(penalty);
    }
}
