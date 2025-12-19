package IotSystem.IoTSystem.Service.Implement;


import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.BorrowingGroup;
import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.StudentGroup;
import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import IotSystem.IoTSystem.Model.Entities.Enum.RequestType;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.Wallet_Transaction_Status;
import IotSystem.IoTSystem.Model.Entities.Enum.Wallet_Transaction_Type;
import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Entities.RequestKitComponent;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import IotSystem.IoTSystem.Model.Mappers.BorrowingRequestMapper;
import IotSystem.IoTSystem.Model.Response.TransactionHistoryResponse;
import IotSystem.IoTSystem.Model.Request.BorrowingRequestCreateRequest;
import IotSystem.IoTSystem.Model.Request.ComponentRentalRequest;
import IotSystem.IoTSystem.Model.Response.BorrowingRequestResponse;
import IotSystem.IoTSystem.Model.Response.BorrowPenaltyStatsResponse;
import IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.BorrowingGroupRepository;
import IotSystem.IoTSystem.Repository.BorrowingRequestRepository;
import IotSystem.IoTSystem.Repository.ClassAssignemntRepository;
import IotSystem.IoTSystem.Repository.KitComponentRepository;
import IotSystem.IoTSystem.Repository.KitsRepository;
import IotSystem.IoTSystem.Repository.PenaltyRepository;
import IotSystem.IoTSystem.Repository.RequestKitComponentRepository;
import IotSystem.IoTSystem.Repository.StudentGroupRepository;
import IotSystem.IoTSystem.Repository.WalletRepository;
import IotSystem.IoTSystem.Repository.WalletTransactionRepository;
import IotSystem.IoTSystem.Service.IBorrowingRequestService;
import IotSystem.IoTSystem.Service.Implement.QRCodeService;
import IotSystem.IoTSystem.Service.WebSocketService;
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

    @Autowired
    private BorrowingGroupRepository borrowingGroupRepository;

    @Autowired
    private PenaltyRepository penaltyRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private ClassAssignemntRepository classAssignemntRepository;

    @Autowired
    private StudentGroupRepository studentGroupRepository;


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

        // Validate student is in active class (for students only)
        validateStudentActiveClass(account);

        // Validate lecturer rental limits
        validateLecturerRentalLimits(account, kit);

        // Validate leader rental limits and active group requirement
        validateLeaderRentalLimits(account);
        validateLeaderActiveGroup(account);

        BorrowingRequest borrow = new BorrowingRequest();
        borrow.setKit(kit);

        // Rental fee is 100% of kit amount
        double deposit_amount = kit.getAmount();

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

        BorrowingRequestResponse response = BorrowingRequestMapper.toResponse(savedBorrow);

        // Send real-time notification to admins about new rental request
        try {
            webSocketService.sendRentalRequestToAdmins(response);
        } catch (Exception e) {
            System.err.println("Error sending WebSocket notification: " + e.getMessage());
        }

        return response;
    }

    @Override
    public BorrowingRequestResponse createComponentRequest(ComponentRentalRequest request) {
        // Get current user from security context
        Account account = getCurrentUser();

        // Validate student is in active class (for students only)
        validateStudentActiveClass(account);

        // Validate leader has active group (for leaders only)
        validateLeaderActiveGroup(account);

        // Get component
        Kit_Component component = kitComponentRepository.findById(request.getKitComponentsId())
                .orElseThrow(() -> new ResourceNotFoundException("Component not found with ID: " + request.getKitComponentsId()));

        // Get kit from component
        Kits kit = component.getKit();
        if (kit == null) {
            throw new RuntimeException("Kit not found for this component");
        }

        // Check availability - but don't subtract yet, wait for admin approval
        if (component.getQuantityAvailable() < request.getQuantity()) {
            throw new RuntimeException("Not enough components available. Required: " + request.getQuantity() + ", Available: " + component.getQuantityAvailable());
        }

        // Don't subtract quantity here - it will be subtracted when admin approves

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

        // Don't save component here - quantity will be updated when admin approves

        BorrowingRequestResponse response = BorrowingRequestMapper.toResponse(savedRequest);

        // Send real-time notification to admins about new component rental request
        try {
            webSocketService.sendRentalRequestToAdmins(response);
        } catch (Exception e) {
            System.err.println("Error sending WebSocket notification: " + e.getMessage());
        }

        return response;
    }

    private Account getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Validate student is in active class
     * Students can only use rent services if they are in an active class
     * If student is in inactive class, they must join a new active class first
     */
    private void validateStudentActiveClass(Account account) {
        // Check if account is a student
        if (account.getRole() == null || !account.getRole().getName().equalsIgnoreCase("STUDENT")) {
            return; // Not a student, skip validation
        }

        // Get all class assignments for this student
        List<ClassAssignment> assignments = classAssignemntRepository.findByAccount(account);

        if (assignments.isEmpty()) {
            throw new RuntimeException("Student is not assigned to any class. Please join a class first to use rent services.");
        }

        // Check if student has at least one active class assignment
        boolean hasActiveClass = assignments.stream()
                .anyMatch(assignment -> assignment.getClazz() != null && assignment.getClazz().isStatus());

        if (!hasActiveClass) {
            throw new RuntimeException("Student is in an inactive class. Please join a new active class to enable rent services.");
        }
    }

    /**
     * Validate lecturer rental limits
     * Lecturers can rent maximum 2 kits: 1 STUDENT_KIT and 1 LECTURER_KIT
     * They cannot rent:
     * - 2 kits of the same type
     * - More than 2 kits total (if they already have both types)
     * Rejected requests are not counted
     */
    private void validateLecturerRentalLimits(Account account, Kits kit) {
        // Check if account is a lecturer
        if (account.getRole() == null || !account.getRole().getName().equalsIgnoreCase("LECTURER")) {
            return; // Not a lecturer, skip validation
        }

        // Get all active borrowing requests for this lecturer (excluding REJECTED and RETURNED)
        List<BorrowingRequest> activeRequests = borrowingRequestRepository.findByRequestedById(account.getId())
                .stream()
                .filter(req -> req.getRequestType() == RequestType.BORROW_KIT) // Only check kit rentals, not component rentals
                .filter(req -> req.getStatus() != null && !req.getStatus().equalsIgnoreCase("REJECTED")) // Exclude rejected requests
                .filter(req -> req.getStatus() != null && !req.getStatus().equalsIgnoreCase("RETURNED")) // Exclude returned requests
                .collect(Collectors.toList());

        // Check if lecturer already has a kit of the same type
        KitType requestedKitType = kit.getType();
        boolean hasSameType = activeRequests.stream()
                .anyMatch(req -> req.getKit() != null && req.getKit().getType() == requestedKitType);

        if (hasSameType) {
            throw new RuntimeException("Lecturer already has an active rental for " + requestedKitType + ". You cannot rent another kit of the same type.");
        }

        // Check if lecturer already has both types rented
        boolean hasStudentKit = activeRequests.stream()
                .anyMatch(req -> req.getKit() != null && req.getKit().getType() == KitType.STUDENT_KIT);
        boolean hasLecturerKit = activeRequests.stream()
                .anyMatch(req -> req.getKit() != null && req.getKit().getType() == KitType.LECTURER_KIT);

        if (hasStudentKit && hasLecturerKit) {
            throw new RuntimeException("Lecturer already has both STUDENT_KIT and LECTURER_KIT rented. Maximum rental limit reached.");
        }
    }

    /**
     * Validate leader rental limits
     * Leaders can rent maximum 1 kit
     * They can only send a new request when their previous request is REJECTED
     */
    private void validateLeaderRentalLimits(Account account) {
        // Check if account is a leader (has LEADER role in any BorrowingGroup)
        boolean isLeader = borrowingGroupRepository.findByAccountId(account.getId())
                .stream()
                .anyMatch(bg -> bg.getRoles() == GroupRoles.LEADER);

        if (!isLeader) {
            return; // Not a leader, skip validation
        }

        // Get all active borrowing requests for this leader (excluding REJECTED and RETURNED)
        List<BorrowingRequest> activeRequests = borrowingRequestRepository.findByRequestedById(account.getId())
                .stream()
                .filter(req -> req.getRequestType() == RequestType.BORROW_KIT) // Only check kit rentals, not component rentals
                .filter(req -> req.getStatus() != null && !req.getStatus().equalsIgnoreCase("REJECTED")) // Exclude rejected requests
                .filter(req -> req.getStatus() != null && !req.getStatus().equalsIgnoreCase("RETURNED")) // Exclude returned requests
                .collect(Collectors.toList());

        // Leader can only have 1 active request at a time
        if (!activeRequests.isEmpty()) {
            throw new RuntimeException("Leader can only rent 1 kit at a time. You already have an active rental request. Please wait until your current request is approved, rejected, or returned before requesting another kit.");
        }
    }

    /**
     * Validate leader has active group
     * Leaders must be in an active group to rent kits
     * If leader's group is inactive (due to class being inactive), they must join a new active group first
     */
    private void validateLeaderActiveGroup(Account account) {
        // Get all borrowing groups for this account with LEADER role
        List<BorrowingGroup> leaderGroups = borrowingGroupRepository.findByAccountId(account.getId())
                .stream()
                .filter(bg -> bg.getRoles() == GroupRoles.LEADER)
                .collect(Collectors.toList());

        if (leaderGroups.isEmpty()) {
            return; // Not a leader, skip validation
        }

        // Check if leader has at least one active group
        // Group must be active AND BorrowingGroup must be active
        boolean hasActiveGroup = leaderGroups.stream()
                .anyMatch(bg -> {
                    if (bg.getStudentGroup() == null) {
                        return false;
                    }
                    StudentGroup studentGroup = bg.getStudentGroup();
                    // Check both StudentGroup status and BorrowingGroup isActive
                    return studentGroup.isStatus() && bg.isActive();
                });

        if (!hasActiveGroup) {
            throw new RuntimeException("Leader must be in an active group to rent kits. Your group has been disabled because the class is inactive. Please join a new active group first.");
        }
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
        qrText.append("Status: ").append(request.getStatus() != null ? request.getStatus() : "PENDING").append("\n");
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
        qrText.append("Status: ").append(request.getStatus() != null ? request.getStatus() : "PENDING").append("\n");
        qrText.append("==================================");
        return qrText.toString();
    }

    @Override
    public BorrowingRequestResponse update(UUID id, BorrowingRequest request) {
        BorrowingRequest existing = borrowingRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing request not found with ID: " + id));

        // Handle status updates (only if status is provided)
        if(request.getStatus() != null && request.getStatus().equals("REJECTED")){
            existing.setStatus("REJECTED");
            // No need to restore quantity because it was never subtracted
        }
        else if(request.getStatus() != null && request.getStatus().equals("APPROVED")){
            existing.setStatus("APPROVED");
            existing.setApprovedDate(LocalDateTime.now());

            // For component rental requests, subtract quantity when approved
            if (existing.getRequestType() == RequestType.BORROW_COMPONENT) {
                List<RequestKitComponent> requestComponents = requestKitComponentRepository.findByRequestId(existing.getId());
                if (!requestComponents.isEmpty()) {
                    for (RequestKitComponent reqComponent : requestComponents) {
                        Kit_Component component = kitComponentRepository.findById(reqComponent.getKitComponentsId())
                                .orElseThrow(() -> new ResourceNotFoundException("Component not found with ID: " + reqComponent.getKitComponentsId()));

                        // Check availability again before subtracting
                        if (component.getQuantityAvailable() < reqComponent.getQuantity()) {
                            throw new RuntimeException("Not enough components available. Required: " + reqComponent.getQuantity() + ", Available: " + component.getQuantityAvailable());
                        }

                        // Subtract quantity when approved
                        component.setQuantityAvailable(component.getQuantityAvailable() - reqComponent.getQuantity());
                        kitComponentRepository.save(component);
                    }
                }
            }

            // Deduct deposit amount from user's wallet
            Account account = existing.getRequestedBy();
            Wallet wallet = account.getWallet();

            if (wallet != null) {
                BigDecimal currentBalance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
                BigDecimal depositAmount = BigDecimal.valueOf(existing.getDepositAmount());

                // Check if wallet has enough balance
                if (currentBalance.compareTo(depositAmount) >= 0) {
                    // Get previous balance before transaction
                    Double previousBalance = currentBalance.doubleValue();

                    BigDecimal newBalance = currentBalance.subtract(depositAmount);
                    wallet.setBalance(newBalance);
                    wallet.setUpdatedAt(LocalDateTime.now());
                    walletRepository.save(wallet);

                    // Create wallet transaction record
                    WalletTransaction transaction = new WalletTransaction();
                    transaction.setAmount(existing.getDepositAmount());
                    transaction.setPreviousBalance(previousBalance);
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
        else if(request.getStatus() != null && request.getStatus().equals("RETURNED")){
            existing.setStatus("RETURNED");
            if(request.getActualReturnDate() != null) {
                existing.setActualReturnDate(request.getActualReturnDate());
            }

            // Restore kit quantity when returned
            if (existing.getKit() != null && existing.getRequestType() == RequestType.BORROW_KIT) {
                Kits kit = existing.getKit();
                kit.setQuantityAvailable(kit.getQuantityAvailable() + 1);

                // If quantity available was 0 and now becomes > 0, set status back to AVAILABLE
                if (kit.getQuantityAvailable() > 0 && kit.getStatus() != null && kit.getStatus().equals("IN_USE")) {
                    kit.setStatus("AVAILABLE");
                }
                kitsRepository.save(kit);
            }

            // Restore component quantity when returned
            if (existing.getRequestType() == RequestType.BORROW_COMPONENT) {
                List<RequestKitComponent> requestComponents = requestKitComponentRepository.findByRequestId(existing.getId());
                if (!requestComponents.isEmpty()) {
                    for (RequestKitComponent reqComponent : requestComponents) {
                        Kit_Component component = kitComponentRepository.findById(reqComponent.getKitComponentsId())
                                .orElseThrow(() -> new ResourceNotFoundException("Component not found with ID: " + reqComponent.getKitComponentsId()));

                        // Restore quantity when returned
                        component.setQuantityAvailable(component.getQuantityAvailable() + reqComponent.getQuantity());
                        kitComponentRepository.save(component);
                    }
                }
            }

            // Process refund if no penalty exists
            // If penalty exists, refund will be handled when penalty is paid
            if (existing.getDepositAmount() != null && existing.getDepositAmount() > 0) {
                // Check if there's an unresolved penalty for this borrowing request
                boolean hasUnresolvedPenalty = penaltyRepository.findAll().stream()
                        .anyMatch(p -> p.getRequest() != null &&
                                p.getRequest().getId().equals(existing.getId()) &&
                                !p.isResolved());

                if (!hasUnresolvedPenalty) {
                    // No penalty, refund full deposit
                    Account account = existing.getRequestedBy();
                    Wallet wallet = account.getWallet();

                    if (wallet != null) {
                        BigDecimal depositAmount = BigDecimal.valueOf(existing.getDepositAmount());
                        BigDecimal currentBalance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;

                        // Get previous balance before transaction
                        Double previousBalance = currentBalance.doubleValue();

                        BigDecimal newBalance = currentBalance.add(depositAmount);

                        wallet.setBalance(newBalance);
                        wallet.setUpdatedAt(LocalDateTime.now());
                        walletRepository.save(wallet);

                        // Create refund transaction
                        WalletTransaction refundTransaction = new WalletTransaction();
                        refundTransaction.setAmount(existing.getDepositAmount());
                        refundTransaction.setPreviousBalance(previousBalance);
                        refundTransaction.setTransactionType(Wallet_Transaction_Type.REFUND);
                        refundTransaction.setTransactionStatus(Wallet_Transaction_Status.COMPLETED);
                        refundTransaction.setDescription("Refund from rental deposit - kit returned without penalty. Borrowing Request: " + existing.getId());
                        refundTransaction.setPaymentMethod("Wallet");
                        refundTransaction.setWallet(wallet);
                        refundTransaction.setCreatedAt(LocalDateTime.now());
                        refundTransaction.setUpdatedAt(LocalDateTime.now());
                        walletTransactionRepository.save(refundTransaction);

                        // Send WebSocket update to user for wallet balance and transaction
                        try {
                            TransactionHistoryResponse transactionResponse = new TransactionHistoryResponse();
                            transactionResponse.setId(refundTransaction.getId());
                            transactionResponse.setType(refundTransaction.getTransactionType().name());
                            transactionResponse.setAmount(refundTransaction.getAmount());
                            transactionResponse.setPreviousBalance(refundTransaction.getPreviousBalance());
                            transactionResponse.setDescription(refundTransaction.getDescription());
                            transactionResponse.setStatus(refundTransaction.getTransactionStatus().name());
                            transactionResponse.setCreatedAt(refundTransaction.getCreatedAt());
                            transactionResponse.setUpdatedAt(refundTransaction.getUpdatedAt());

                            // Send wallet update with new balance
                            java.util.Map<String, Object> walletUpdate = new java.util.HashMap<>();
                            walletUpdate.put("balance", newBalance.doubleValue());
                            walletUpdate.put("transaction", transactionResponse);

                            webSocketService.sendWalletUpdateToUser(account.getId().toString(), walletUpdate);
                            webSocketService.sendWalletTransactionToUser(account.getId().toString(), transactionResponse);

                            System.out.println("Refunded " + depositAmount + " VND to wallet. New balance: " + newBalance);
                            System.out.println("WebSocket wallet update sent to user: " + account.getId());
                        } catch (Exception e) {
                            System.err.println("Error sending WebSocket wallet update: " + e.getMessage());
                            // Continue even if WebSocket fails
                        }
                    }
                }
                // If penalty exists, refund will be handled in confirmPaymentForPenalty method
            }
        }

        // Update other fields if provided (even if status is null - for partial updates like isLate)
        if(request.getStatus() != null){
            existing.setStatus(request.getStatus());
        }
        if(request.getActualReturnDate() != null){
            existing.setActualReturnDate(request.getActualReturnDate());
        }
        if(request.getIsLate() != null){
            existing.setIsLate(request.getIsLate());
        }

        if(request.getNote() != null){
            existing.setNote(request.getNote());
        }

        BorrowingRequest updated = borrowingRequestRepository.save(existing);
        BorrowingRequestResponse response = BorrowingRequestMapper.toResponse(updated);

        // Send real-time update to admins
        try {
            webSocketService.sendRentalRequestToAdmins(response);
        } catch (Exception e) {
            System.err.println("Error sending WebSocket notification: " + e.getMessage());
        }

        // Send real-time update to the user who made the request
        if (updated.getRequestedBy() != null && updated.getRequestedBy().getId() != null) {
            try {
                webSocketService.sendRentalRequestUpdateToUser(
                        updated.getRequestedBy().getId().toString(),
                        response
                );
            } catch (Exception e) {
                System.err.println("Error sending WebSocket notification to user: " + e.getMessage());
            }
        }

        return response;
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

    @Override
    public List<BorrowPenaltyStatsResponse> getBorrowPenaltyStats() {
        List<BorrowingRequestRepository.BorrowPenaltyStats> projections = borrowingRequestRepository.aggregateBorrowAndPenalty();
        return projections.stream()
                .map(proj -> new BorrowPenaltyStatsResponse(
                        proj.getTotalBorrow(),
                        proj.getTotalPenalty(),
                        proj.getStatDate()
                ))
                .collect(Collectors.toList());
    }
}
