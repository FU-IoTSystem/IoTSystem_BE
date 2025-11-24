package IotSystem.IoTSystem.Service.Implement;


import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Model.Entities.Enum.RequestType;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.Wallet_Transaction_Status;
import IotSystem.IoTSystem.Model.Entities.Enum.Wallet_Transaction_Type;
import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Entities.RequestKitComponent;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import IotSystem.IoTSystem.Model.Mappers.BorrowingRequestMapper;
import IotSystem.IoTSystem.Model.Request.BorrowingRequestCreateRequest;
import IotSystem.IoTSystem.Model.Request.ComponentRentalRequest;
import IotSystem.IoTSystem.Model.Response.BorrowingRequestResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.BorrowingRequestRepository;
import IotSystem.IoTSystem.Repository.KitComponentRepository;
import IotSystem.IoTSystem.Repository.KitsRepository;
import IotSystem.IoTSystem.Repository.RequestKitComponentRepository;
import IotSystem.IoTSystem.Repository.WalletRepository;
import IotSystem.IoTSystem.Repository.WalletTransactionRepository;
import IotSystem.IoTSystem.Service.IBorrowingRequestService;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BorrowingRequestServiceImpl implements IBorrowingRequestService {

    @Autowired
    private KitsRepository kitsRepository;

    @Autowired
    private BorrowingRequestRepository borrowingRequestRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private KitComponentRepository kitComponentRepository;

    @Autowired
    private RequestKitComponentRepository requestKitComponentRepository;


    @Override
    public List<BorrowingRequestResponse> getAll() {
        List<BorrowingRequest> borrow = borrowingRequestRepository.findByStatus("PENDING");

        return borrow.stream()
                .map(BorrowingRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BorrowingRequestResponse create(BorrowingRequestCreateRequest request) {
        Kits kit = kitsRepository.findById(request.getKitId()).orElseThrow(() ->
                new ResourceNotFoundException("Did not found Kit with ID: " + request.getKitId()));

        Account account = accountRepository.findById(request.getAccountID()).orElseThrow(() ->
                new ResourceNotFoundException("Did not found Account with ID: " + request.getAccountID()));

        BorrowingRequest borrow = new BorrowingRequest();
        borrow.setKit(kit);

        double deposit_amount = kit.getAmount() / 2.0;

        borrow.setDepositAmount(deposit_amount);
        kit.setQuantityAvailable(kit.getQuantityAvailable() - 1);

        // If quantity available becomes zero, set kit status to IN_USE
        if (kit.getQuantityAvailable() == 0) {
            kit.setStatus("IN_USE");
        }

        borrow.setRequestedBy(account);
        borrow.setExpectReturnDate(request.getExpectReturnDate());
        borrow.setReason(request.getReason());
        borrow.setStatus("PENDING");
        borrow.setRequestType(request.getRequestType());

        // Generate QR code
        try {
            String qrCodeData = generateQRCodeText(account, kit, borrow);
            String qrCodeBase64 = QRCodeService.generateQRCodeBase64(qrCodeData);
            borrow.setQrCode(qrCodeBase64);
        } catch (WriterException | IOException e) {
            System.err.println("Error generating QR code: " + e.getMessage());
            // Continue without QR code if generation fails
        }
        kitsRepository.save(kit);
        BorrowingRequest savedBorrow = borrowingRequestRepository.save(borrow);

        return BorrowingRequestMapper.toResponse(savedBorrow);
    }

    @Override
    public BorrowingRequestResponse createComponentRequest(ComponentRentalRequest request) {
        // Get current user from security context
        Account account = getCurrentUser();

        // Get component
        Kit_Component component = kitComponentRepository.findById(request.getKitComponentsId())
                .orElseThrow(() -> new ResourceNotFoundException("Component not found with ID: " + request.getKitComponentsId()));

        component.setQuantityAvailable(component.getQuantityAvailable() - request.getQuantity());
        // Get kit from component
        Kits kit = component.getKit();
        if (kit == null) {
            throw new RuntimeException("Kit not found for this component");
        }

        // Check availability
        if (component.getQuantityAvailable() < request.getQuantity()) {
            throw new RuntimeException("Not enough components available. Required: " + request.getQuantity() + ", Available: " + component.getQuantityAvailable());
        }


        // Create borrowing request for component
        BorrowingRequest borrowingRequest = new BorrowingRequest();
        borrowingRequest.setRequestedBy(account);
        borrowingRequest.setKit(kit); // Set kit reference
        borrowingRequest.setReason(request.getReason());
        borrowingRequest.setDepositAmount(request.getDepositAmount());
        borrowingRequest.setExpectReturnDate(request.getExpectReturnDate());
        borrowingRequest.setStatus("PENDING");
        borrowingRequest.setRequestType(RequestType.BORROW_COMPONENT);

        // Generate QR code for component rental
        try {
            String qrCodeData = generateComponentQRCodeText(account, component, borrowingRequest, request);
            String qrCodeBase64 = QRCodeService.generateQRCodeBase64(qrCodeData);
            borrowingRequest.setQrCode(qrCodeBase64);
        } catch (Exception e) {
            System.err.println("Error generating QR code: " + e.getMessage());
        }

        BorrowingRequest savedRequest = borrowingRequestRepository.save(borrowingRequest);

        // Create RequestKitComponent to link component with request
        RequestKitComponent requestKitComponent = new RequestKitComponent();
        requestKitComponent.setRequestId(savedRequest.getId());
        requestKitComponent.setKitComponentsId(request.getKitComponentsId());
        requestKitComponent.setQuantity(request.getQuantity());
        requestKitComponent.setComponentName(request.getComponentName());
        requestKitComponent.setCreatedAt(LocalDateTime.now());
        requestKitComponent.setUpdatedAt(LocalDateTime.now());
        requestKitComponentRepository.save(requestKitComponent);

        // Decrease available quantity
        component.setQuantityAvailable(component.getQuantityAvailable() - request.getQuantity());
        kitComponentRepository.save(component);

        return BorrowingRequestMapper.toResponse(savedRequest);
    }

    private Account getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String generateQRCodeText(Account account, Kits kit, BorrowingRequest request) {
        StringBuilder qrText = new StringBuilder();
        qrText.append("=== BORROWING REQUEST INFO ===\n");
        qrText.append("Borrower: ").append(account.getFullName()).append("\n");
        qrText.append("Borrower ID: ").append(account.getId()).append("\n");
        qrText.append("Kit Name: ").append(kit.getKitName()).append("\n");
        qrText.append("Kit ID: ").append(kit.getId()).append("\n");
        qrText.append("Request Type: ").append(request.getRequestType()).append("\n");
        qrText.append("Reason: ").append(request.getReason()).append("\n");
        qrText.append("Expected Return Date: ").append(request.getExpectReturnDate()).append("\n");
        qrText.append("Status: ").append(request.getStatus()).append("\n");
        qrText.append("==============================");
        return qrText.toString();
    }

    private String generateComponentQRCodeText(Account account, Kit_Component component, BorrowingRequest request, ComponentRentalRequest componentRequest) {
        StringBuilder qrText = new StringBuilder();
        qrText.append("=== COMPONENT RENTAL REQUEST ===\n");
        qrText.append("Borrower: ").append(account.getFullName()).append("\n");
        qrText.append("Borrower ID: ").append(account.getId()).append("\n");
        qrText.append("Component Name: ").append(componentRequest.getComponentName()).append("\n");
        qrText.append("Component ID: ").append(component.getId()).append("\n");
        qrText.append("Quantity: ").append(componentRequest.getQuantity()).append("\n");
        qrText.append("Price per Unit: ").append(component.getPricePerCom()).append(" VND\n");
        qrText.append("Total Amount: ").append(request.getDepositAmount()).append(" VND\n");
        qrText.append("Reason: ").append(request.getReason()).append("\n");
        qrText.append("Expected Return Date: ").append(request.getExpectReturnDate()).append("\n");
        qrText.append("Status: ").append(request.getStatus()).append("\n");
        qrText.append("==================================");
        return qrText.toString();
    }

    @Override
    public BorrowingRequestResponse update(UUID id, BorrowingRequest request) {
        BorrowingRequest existing = borrowingRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing request not found with ID: " + id));

        if(request.getStatus().equals("REJECTED")){
            existing.setStatus("REJECTED");
        }
        else if(request.getStatus().equals("APPROVED")){
            existing.setStatus("APPROVED");
            existing.setApprovedDate(LocalDateTime.now());

            // Deduct deposit amount from user's wallet
            Account account = existing.getRequestedBy();
            Wallet wallet = account.getWallet();

            if (wallet != null) {
                BigDecimal currentBalance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
                BigDecimal depositAmount = BigDecimal.valueOf(existing.getDepositAmount());

                // Check if wallet has enough balance
                if (currentBalance.compareTo(depositAmount) >= 0) {
                    BigDecimal newBalance = currentBalance.subtract(depositAmount);
                    wallet.setBalance(newBalance);
                    wallet.setUpdatedAt(LocalDateTime.now());
                    walletRepository.save(wallet);

                    // Create wallet transaction record
                    WalletTransaction transaction = new WalletTransaction();
                    transaction.setAmount(existing.getDepositAmount());
                    transaction.setTransactionType(Wallet_Transaction_Type.RENTAL_FEE);
                    transaction.setTransactionStatus(Wallet_Transaction_Status.COMPLETED);

                    // Check if this is a component rental or kit rental
                    String description;
                    if (existing.getKit() != null) {
                        // Kit rental
                        description = "Rental fee deposit for kit: " + existing.getKit().getKitName();
                    } else {
                        // Component rental - get component name from RequestKitComponent
                        List<RequestKitComponent> components = requestKitComponentRepository.findByRequestId(existing.getId());
                        if (!components.isEmpty()) {
                            RequestKitComponent reqComponent = components.get(0);
                            description = "Rental fee deposit for component: " + reqComponent.getComponentName() + " (Qty: " + reqComponent.getQuantity() + ")";
                        } else {
                            description = "Rental fee deposit";
                        }
                    }
                    transaction.setDescription(description);
                    transaction.setPaymentMethod("Wallet");
                    transaction.setWallet(wallet);
                    transaction.setCreatedAt(LocalDateTime.now());
                    transaction.setUpdatedAt(LocalDateTime.now());
                    walletTransactionRepository.save(transaction);

                    System.out.println("Deducted " + depositAmount + " VND from wallet. New balance: " + newBalance);
                } else {
                    throw new RuntimeException("Insufficient wallet balance. Required: " + depositAmount + ", Available: " + currentBalance);
                }
            } else {
                throw new RuntimeException("User wallet not found");
            }
        }
        else {
            existing.setStatus(request.getStatus());
            existing.setActualReturnDate(request.getActualReturnDate());
        }

        if(request.getNote() != null){
            existing.setNote(request.getNote());
        }

        BorrowingRequest updated = borrowingRequestRepository.save(existing);
        return BorrowingRequestMapper.toResponse(updated);
    }

    @Override
    public void delete(UUID id) {
        BorrowingRequest borrow = borrowingRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing request not found with ID: " + id));
        borrowingRequestRepository.delete(borrow);
    }

    @Override
    public List<BorrowingRequestResponse> getByStatus() {

        List<BorrowingRequest> borrow = borrowingRequestRepository.findByStatus("APPROVED");

        return borrow.stream().map(BorrowingRequestMapper::toResponse).toList();
    }

    @Override
    public BorrowingRequestResponse getById(UUID id) {
        BorrowingRequest borrow = borrowingRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing request not found with ID: " + id));
        return BorrowingRequestMapper.toResponse(borrow);
    }

    @Override
    public List<BorrowingRequestResponse> getByUser(UUID userID) {
        List<BorrowingRequest> borrows = borrowingRequestRepository.findByRequestedById(userID);
        return borrows.stream()
                .map(BorrowingRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowingRequestResponse> getByStatuses(List<String> statuses) {
        List<BorrowingRequest> borrows = borrowingRequestRepository.findByStatusIn(statuses);
        return borrows.stream()
                .map(BorrowingRequestMapper::toResponse)
                .collect(Collectors.toList());
    }
}
