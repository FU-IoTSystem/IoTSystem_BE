package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import IotSystem.IoTSystem.Model.Mappers.AccountMapper;
import IotSystem.IoTSystem.Model.Mappers.ResponseRegisterMapper;
import IotSystem.IoTSystem.Model.Request.AccountRequest;
import IotSystem.IoTSystem.Model.Request.ChangePasswordRequest;
import IotSystem.IoTSystem.Model.Request.LoginRequest;
import IotSystem.IoTSystem.Model.Request.RegisterRequest;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Roles;
import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Request.UpdateAccountRequest;
import IotSystem.IoTSystem.Model.Response.ProfileResponse;
import IotSystem.IoTSystem.Model.Response.RegisterResponse;
import IotSystem.IoTSystem.Model.Response.StudentExportResponse;
import IotSystem.IoTSystem.Security.TokenProvider;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.RolesRepository;
import IotSystem.IoTSystem.Repository.BorrowingGroupRepository;
import IotSystem.IoTSystem.Repository.StudentGroupRepository;
import IotSystem.IoTSystem.Repository.ClassesRepository;
import IotSystem.IoTSystem.Repository.ClassAssignemntRepository;
import IotSystem.IoTSystem.Repository.PenaltyRepository;
import IotSystem.IoTSystem.Repository.BorrowingRequestRepository;
import IotSystem.IoTSystem.Repository.DamageReportRepository;
import IotSystem.IoTSystem.Repository.WalletRepository;
import IotSystem.IoTSystem.Repository.WalletTransactionRepository;
import IotSystem.IoTSystem.Repository.PenaltyDetailRepository;
import IotSystem.IoTSystem.Service.IAccountService;
import IotSystem.IoTSystem.Service.INotificationService;
import IotSystem.IoTSystem.Model.Request.NotificationRequest;
import IotSystem.IoTSystem.Model.Entities.Enum.NotificationSubType;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountServiceImpl implements IAccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private BorrowingGroupRepository borrowingGroupRepository;

    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private ClassAssignemntRepository classAssignmentRepository;

    @Autowired
    private PenaltyRepository penaltyRepository;

    @Autowired
    private BorrowingRequestRepository borrowingRequestRepository;

    @Autowired
    private DamageReportRepository damageReportRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private PenaltyDetailRepository penaltyDetailRepository;

    @Autowired
    private INotificationService notificationService;


    //lấy user hiện tại của hệ thống
    private Account getCurrentAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Validate phone number format
     * Phone number must: start with 0, have exactly 10 digits, contain only numbers
     * @param phoneNumber The phone number to validate
     * @throws RuntimeException if phone number format is invalid
     */
    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return; // Allow empty phone numbers (optional field)
        }

        String trimmedPhone = phoneNumber.trim();

        // Check if phone number starts with 0
        if (!trimmedPhone.startsWith("0")) {
            throw new RuntimeException("Phone number must start with 0");
        }

        // Check if phone number contains only digits
        if (!trimmedPhone.matches("^[0-9]+$")) {
            throw new RuntimeException("Phone number must contain only numbers");
        }

        // Check if phone number has exactly 10 digits
        if (trimmedPhone.length() != 10) {
            throw new RuntimeException("Phone number must have exactly 10 digits");
        }
    }

    public String login(LoginRequest loginRequest) {
        // Xác thực người dùng
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Lấy user từ DB
        Account account = accountRepository.findByEmail(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Kiểm tra trạng thái tài khoản
        if (Boolean.FALSE.equals(account.getIsActive())) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        }

        // Lấy role làm quyền truy cập (dạng ROLE_USER, ROLE_ADMIN...)
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + account.getRole().getName()));

        // Tạo đối tượng UserDetails
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPasswordHash(),
                authorities
        );

        // Sinh token JWT
        return tokenProvider.generateToken(userDetails);
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        Roles role = rolesRepository.findByName(request.getRoles().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Default role" + request.getRoles().toUpperCase() + " not found"));

        // Validate phone number format
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            validatePhoneNumber(request.getPhoneNumber());
        }

        // Tạo tài khoản trước
        Account account = new Account();
        account.setEmail(request.getUsername());
        account.setFullName(request.getFullName());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setPhone(request.getPhoneNumber());
        account.setIsActive(true);
        account.setRole(role);

        if(role.getName().equals("STUDENT")){
            account.setStudentCode(request.getStudentCode());
            account.setLecturerCode(null);
        }

        if(role.getName().equals("LECTURER")){
            account.setLecturerCode(request.getLecturerCode());
            account.setStudentCode(null);
        }

        // Nếu là STUDENT hoặc LECTURER thì tạo ví
        if (role.getName().equals("STUDENT") || role.getName().equals("LECTURER")) {
            Wallet wallet = new Wallet();
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setCurrency("VND");
            wallet.setActive(true);
            wallet.setAccount(account); // Gắn account vào wallet

            account.setWallet(wallet);

        }
        accountRepository.save(account); // ✅ cascade sẽ tự lưu Wallet
        return ResponseRegisterMapper.toResponse(account);
    }

    @Override
    public RegisterResponse updating(RegisterRequest request, UUID id){
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Did not found Account ID: " + id));

        // Validate email uniqueness (excluding current account)
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            String newEmail = request.getUsername().trim();
            // Check if email is being changed
            if (!newEmail.equals(account.getEmail())) {
                if (accountRepository.existsByEmailExcludingId(newEmail, id)) {
                    throw new RuntimeException("Email already exists: " + newEmail);
                }
            }
        }

        // Validate studentCode/lecturerCode uniqueness (excluding current account) - only for STUDENT/LECTURER role
        Roles role = rolesRepository.findByName(request.getRoles().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Did not found the role: " + request.getRoles()));

        if(role.getName().equals("STUDENT")){
            if (request.getStudentCode() != null && !request.getStudentCode().trim().isEmpty()) {
                String newStudentCode = request.getStudentCode().trim();
                // Check if studentCode is being changed
                if (account.getStudentCode() == null || !newStudentCode.equals(account.getStudentCode())) {
                    if (accountRepository.existsByStudentCodeExcludingId(newStudentCode, id)) {
                        throw new RuntimeException("Student Code already exists: " + newStudentCode);
                    }
                }
            }
        }

        if(role.getName().equals("LECTURER")){
            if (request.getLecturerCode() != null && !request.getLecturerCode().trim().isEmpty()) {
                String newLecturerCode = request.getLecturerCode().trim();
                // Check if lecturerCode is being changed
                if (account.getLecturerCode() == null || !newLecturerCode.equals(account.getLecturerCode())) {
                    if (accountRepository.existsByLecturerCodeExcludingId(newLecturerCode, id)) {
                        throw new RuntimeException("Lecturer Code already exists: " + newLecturerCode);
                    }
                }
            }
        }

        // Validate phone uniqueness and format (excluding current account)
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            String newPhone = request.getPhoneNumber().trim();

            // Validate phone number format
            validatePhoneNumber(newPhone);

            // Check if phone is being changed
            if (account.getPhone() == null || !newPhone.equals(account.getPhone())) {
                if (accountRepository.existsByPhoneExcludingId(newPhone, id)) {
                    throw new RuntimeException("Phone number already exists: " + newPhone);
                }
            }
        }

        // Update account fields
        account.setPhone(request.getPhoneNumber());
        account.setEmail(request.getUsername());
        account.setFullName(request.getFullName());

        // Update role - only admin can update role
        account.setRole(role);

        if(role.getName().equals("STUDENT")){
            account.setStudentCode(request.getStudentCode());
            account.setLecturerCode(null); // Clear lecturerCode if changing from lecturer to student
        }

        if(role.getName().equals("LECTURER")){
            account.setLecturerCode(request.getLecturerCode());
            account.setStudentCode(null); // Clear studentCode if changing from student to lecturer
        }

        accountRepository.save(account);

        // If classCode is provided and account is STUDENT, update class assignment
        if (role.getName().equals("STUDENT") && request.getClassCode() != null && !request.getClassCode().trim().isEmpty()) {
            updateStudentClassAssignment(account, request.getClassCode().trim());
        } else if (role.getName().equals("STUDENT") && (request.getClassCode() == null || request.getClassCode().trim().isEmpty())) {
            // If classCode is empty/null, remove all class assignments for this student
            removeAllClassAssignmentsForStudent(account);
        }

        // If classCode is provided and account is LECTURER, assign lecturer to class
        if (role.getName().equals("LECTURER") && request.getClassCode() != null && !request.getClassCode().trim().isEmpty()) {
            assignLecturerToClass(account, request.getClassCode().trim());
        }

        return ResponseRegisterMapper.toResponse(account);

    }

    @Override
    public ProfileResponse updateProfile(UpdateAccountRequest request) {
        Account account = getCurrentAccount();

        // Validate phone number format if provided
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            validatePhoneNumber(request.getPhone());
        }

        account.setFullName(request.getFullName());
        account.setAvatarUrl(request.getAvatarUrl());
        account.setPhone(request.getPhone());

        Account saved = accountRepository.save(account);

        String studentCode = saved.getRole().getName().equals("STUDENT") ? saved.getStudentCode() : null;
        String lecturerCode = saved.getRole().getName().equals("LECTURER") ? saved.getLecturerCode() : null;

        return new ProfileResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getAvatarUrl(),
                saved.getPhone(),
                studentCode,
                lecturerCode,
                saved.getRole().getName(),
                saved.getCreatedAt(),
                saved.getIsActive()
        );
    }

    @Override
    public String changePassword(ChangePasswordRequest request) {
        Account account = getCurrentAccount();

        // Validate old password
        if (!passwordEncoder.matches(request.getOldPassword(), account.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Validate new password
        String newPassword = request.getNewPassword();
        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }
        if (!newPassword.matches(".*[A-Z].*")) {
            throw new RuntimeException("Password must contain at least one uppercase letter");
        }
        if (!newPassword.matches(".*[a-z].*")) {
            throw new RuntimeException("Password must contain at least one lowercase letter");
        }
        if (!newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new RuntimeException("Password must contain at least one special character");
        }

        // Check if new password is same as old password
        if (passwordEncoder.matches(newPassword, account.getPasswordHash())) {
            throw new RuntimeException("New password must be different from old password");
        }

        // Update password
        account.setPasswordHash(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        return "Password changed successfully";
    }
    @Override
    public ProfileResponse getProfile() {
        Account account = getCurrentAccount(); // lấy từ SecurityContext
        return AccountMapper.toProfileResponse(account);
    }

    @Override
    public List<ProfileResponse> getAllAccounts() {
        List<Account> list = accountRepository.findAllExceptAdmin();

        return list.stream().map(AccountMapper::toProfileResponse).toList();
    }

    @Override
    public List<ProfileResponse> getAllStudent(){
        Roles role = rolesRepository.findByName("STUDENT").orElseThrow();
        List<Account> accounts = accountRepository.findByRole(role);

        return accounts.stream().map(AccountMapper::toProfileResponse).toList();
    }

    @Override
    public List<ProfileResponse> getStudentsByClassCode(String classCode) {
        // Find class by classCode
        Classes clazz = classesRepository.findByClassCode(classCode)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with classCode: " + classCode));

        // Get all student assignments for this class
        List<ClassAssignment> assignments = classAssignmentRepository.findStudentAssignmentsByClass(clazz);

        // Extract accounts from assignments and map to ProfileResponse
        return assignments.stream()
                .map(ClassAssignment::getAccount)
                .filter(account -> account != null && account.getIsActive())
                .map(AccountMapper::toProfileResponse)
                .toList();
    }

    @Override
    public List<ProfileResponse> getStudentsByClassId(UUID classId) {
        // Find class by classId
        Classes clazz = classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        // Get all student assignments for this class
        List<ClassAssignment> assignments = classAssignmentRepository.findStudentAssignmentsByClass(clazz);

        // Extract accounts from assignments and map to ProfileResponse
        return assignments.stream()
                .map(ClassAssignment::getAccount)
                .filter(account -> account != null && account.getIsActive())
                .map(AccountMapper::toProfileResponse)
                .toList();
    }

    @Override
    public List<StudentExportResponse> exportStudents() {
        // TODO: consider filtering by class or other conditions if needed
        List<ClassAssignment> assignments = classAssignmentRepository.findAll().stream()
                .filter(ca -> ca.getAccount() != null
                        && ca.getAccount().getRole() != null
                        && "STUDENT".equalsIgnoreCase(ca.getAccount().getRole().getName()))
                .toList();

        return assignments.stream().map(ca -> {
            Account account = ca.getAccount();
            Classes clazz = ca.getClazz();

            StudentExportResponse response = new StudentExportResponse();
            response.setClassCode(clazz != null ? clazz.getClassCode() : null);
            response.setStudentCode(account != null ? account.getStudentCode() : null);
            response.setName(account != null ? account.getFullName() : null);
            response.setEmail(account != null ? account.getEmail() : null);
            response.setRole(account != null && account.getRole() != null ? account.getRole().getName() : null);
            response.setStatus(account != null ? account.getIsActive() : null);
            response.setCreateDate(account != null ? account.getCreatedAt() : null);

            return response;
        }).toList();
    }

    @Override
    public ProfileResponse getAccountById(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return AccountMapper.toProfileResponse(account);
    }
    @Override
    public List<ProfileResponse> getAllbyRoleLecture(){

        Roles role = rolesRepository.findByName("LECTURER").orElseThrow(()
                -> new ResourceNotFoundException("Did not found the role named: Lecturer"));

        List<Account> lecturers = accountRepository.findByRole(role);

        return lecturers.stream().map(AccountMapper::toProfileResponse).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProfileResponse createAStudent(RegisterRequest request) {
        Account account = new Account();
        if (accountRepository.existsByEmail(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Validate phone number format
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            validatePhoneNumber(request.getPhoneNumber());
        }

        Roles role = rolesRepository.findByName("STUDENT").orElseThrow();


        account.setStudentCode(request.getStudentCode());
        account.setRole(role);
        account.setPhone(request.getPhoneNumber());
        account.setFullName(request.getFullName());
        account.setEmail(request.getUsername());

        // password default la email cua sinh vien

//        account.setPasswordHash(passwordEncoder.encode(request.getUsername()));
        account.setPasswordHash(passwordEncoder.encode("1"));
        account.setIsActive(true);

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("VND");
        wallet.setActive(true);
        wallet.setAccount(account); // Gắn account vào wallet

        account.setWallet(wallet);

        accountRepository.save(account);

        // If classCode is provided, assign student to class
        if (request.getClassCode() != null && !request.getClassCode().trim().isEmpty()) {
            assignStudentToClass(account, request.getClassCode().trim());
        }

        return AccountMapper.toProfileResponse(account);
    }

    /**
     * Update student class assignment by classCode
     * Removes all existing class assignments for the student and creates a new one
     * If class doesn't exist, create it with a random lecturer
     * @param account The student account to assign
     * @param classCode The class code to assign the student to
     */
    private void updateStudentClassAssignment(Account account, String classCode) {
        // First, remove all existing class assignments for this student
        removeAllClassAssignmentsForStudent(account);

        // Then assign student to the new class
        assignStudentToClass(account, classCode);
    }

    /**
     * Remove all class assignments for a student
     * @param account The student account
     */
    private void removeAllClassAssignmentsForStudent(Account account) {
        try {
            List<ClassAssignment> existingAssignments = classAssignmentRepository.findStudentAssignmentsByAccount(account);
            for (ClassAssignment assignment : existingAssignments) {
                classAssignmentRepository.delete(assignment);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not remove existing class assignments for student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Assign student to class by classCode
     * If class doesn't exist, create it with a random lecturer
     * @param account The student account to assign
     * @param classCode The class code to assign the student to
     */
    private void assignStudentToClass(Account account, String classCode) {
        try {
            // Find class by class code, or create it if it doesn't exist
            Classes clazz = classesRepository.findByClassCode(classCode).orElseGet(() -> {
                // Create new class if it doesn't exist
                Classes newClass = new Classes();
                newClass.setClassCode(classCode);
                newClass.setSemester(null); // Default to null if not specified
                newClass.setStatus(true); // Default to active

                // Set a random lecturer as the teacher for this class
                List<Account> lecturers = accountRepository.findAll().stream()
                        .filter(acc -> acc.getRole() != null && "LECTURER".equals(acc.getRole().getName()))
                        .filter(acc -> acc.getIsActive() != null && acc.getIsActive())
                        .collect(java.util.stream.Collectors.toList());

                if (lecturers.isEmpty()) {
                    throw new RuntimeException("No active lecturers found. Cannot create class without a lecturer.");
                }

                Account randomLecturer = lecturers.get(new java.util.Random().nextInt(lecturers.size()));
                newClass.setAccount(randomLecturer);

                Classes savedClass = classesRepository.save(newClass);

                // Create ClassAssignment for lecturer when creating new class
                Optional<ClassAssignment> existingLecturerAssignment =
                        classAssignmentRepository.findByClazzAndAccount(savedClass, randomLecturer);

                if (existingLecturerAssignment.isEmpty()) {
                    ClassAssignment lecturerAssignment = new ClassAssignment();
                    lecturerAssignment.setClazz(savedClass);
                    lecturerAssignment.setAccount(randomLecturer);
                    lecturerAssignment.setRole(randomLecturer.getRole());
                    classAssignmentRepository.save(lecturerAssignment);
                }

                return savedClass;
            });

            // Check if assignment already exists for student
            Optional<ClassAssignment> existingAssignment =
                    classAssignmentRepository.findByClazzAndAccount(clazz, account);

            if (existingAssignment.isEmpty()) {
                // Create new class assignment for student
                ClassAssignment assignment = new ClassAssignment();
                assignment.setClazz(clazz);
                assignment.setAccount(account);
                assignment.setRole(account.getRole());
                classAssignmentRepository.save(assignment);
            }
        } catch (Exception e) {
            // Log error but don't fail the entire student creation
            // If class assignment fails, student is still created
            System.err.println("Warning: Could not assign student to class '" + classCode + "': " + e.getMessage());
            e.printStackTrace();
            // Optionally, you can throw exception to rollback student creation
            // throw new RuntimeException("Could not assign student to class '" + classCode + "': " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProfileResponse createALecturer(RegisterRequest request) {
        Account account = new Account();
        if (accountRepository.existsByEmail(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Validate phone number format
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            validatePhoneNumber(request.getPhoneNumber());
        }

        Roles role = rolesRepository.findByName("LECTURER").orElseThrow();

        account.setLecturerCode(request.getLecturerCode());
        account.setStudentCode(null); // Clear studentCode for lecturer
        account.setRole(role);
        account.setPhone(request.getPhoneNumber());
        account.setFullName(request.getFullName());
        account.setEmail(request.getUsername());


//        account.setPasswordHash(passwordEncoder.encode(request.getUsername()));
        account.setPasswordHash(passwordEncoder.encode("1"));
        account.setIsActive(true);

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("VND");
        wallet.setActive(true);
        wallet.setAccount(account); // Gắn account vào wallet

        account.setWallet(wallet);

        accountRepository.save(account);

        // If classCode is provided, create class and assign lecturer to it
        if (request.getClassCode() != null && !request.getClassCode().trim().isEmpty()) {
            assignLecturerToClass(account, request.getClassCode().trim());
        }

        return AccountMapper.toProfileResponse(account);
    }

    /**
     * Assign lecturer to class by classCode
     * If class doesn't exist, create it with this lecturer as teacher
     * @param account The lecturer account to assign
     * @param classCode The class code to assign the lecturer to
     */
    private void assignLecturerToClass(Account account, String classCode) {
        try {
            // Find class by class code, or create it if it doesn't exist
            Classes clazz = classesRepository.findByClassCode(classCode).orElseGet(() -> {
                // Create new class if it doesn't exist
                Classes newClass = new Classes();
                newClass.setClassCode(classCode);
                newClass.setSemester(null); // Default to null if not specified
                newClass.setStatus(true); // Default to active
                newClass.setAccount(account); // Set lecturer as teacher

                Classes savedClass = classesRepository.save(newClass);

                // Create ClassAssignment for lecturer when creating new class
                Optional<ClassAssignment> existingLecturerAssignment =
                        classAssignmentRepository.findByClazzAndAccount(savedClass, account);

                if (existingLecturerAssignment.isEmpty()) {
                    ClassAssignment lecturerAssignment = new ClassAssignment();
                    lecturerAssignment.setClazz(savedClass);
                    lecturerAssignment.setAccount(account);
                    lecturerAssignment.setRole(account.getRole());
                    classAssignmentRepository.save(lecturerAssignment);
                }

                return savedClass;
            });

            // Check if assignment already exists for lecturer
            Optional<ClassAssignment> existingAssignment =
                    classAssignmentRepository.findByClazzAndAccount(clazz, account);

            if (existingAssignment.isEmpty()) {
                // Create new class assignment for lecturer
                ClassAssignment assignment = new ClassAssignment();
                assignment.setClazz(clazz);
                assignment.setAccount(account);
                assignment.setRole(account.getRole());
                classAssignmentRepository.save(assignment);

                // Also update the class's teacher if it doesn't have one
                if (clazz.getAccount() == null) {
                    clazz.setAccount(account);
                    classesRepository.save(clazz);
                }
            }
        } catch (Exception e) {
            // Log error but don't fail the entire lecturer creation
            System.err.println("Warning: Could not assign lecturer to class '" + classCode + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Check if account is ADMIN - prevent deleting admin accounts
        if (account.getRole() != null && "ADMIN".equalsIgnoreCase(account.getRole().getName())) {
            throw new RuntimeException("Cannot delete admin account");
        }

        try {
            // Delete all related records before deleting the account
            // Order matters: delete child entities first, then parent

            // 1. Delete BorrowingGroups (using existing query method)
            try {
                borrowingGroupRepository.findByAccountId(accountId).forEach(borrowingGroupRepository::delete);
                borrowingGroupRepository.flush(); // Force flush to catch constraint violations early
            } catch (Exception e) {
                System.err.println("Warning: Error deleting borrowing groups for account " + accountId + ": " + e.getMessage());
                throw new RuntimeException("Failed to delete borrowing groups: " + e.getMessage(), e);
            }

            // 2. Delete StudentGroups (where account is leader/owner)
            try {
                studentGroupRepository.findAll().stream()
                        .filter(sg -> sg.getAccount() != null && sg.getAccount().getId().equals(accountId))
                        .forEach(studentGroupRepository::delete);
                studentGroupRepository.flush();
            } catch (Exception e) {
                System.err.println("Warning: Error deleting student groups for account " + accountId + ": " + e.getMessage());
                throw new RuntimeException("Failed to delete student groups: " + e.getMessage(), e);
            }

            // 3. Update Classes (where account is teacher) - set account to null and status to inactive
            // If account is LECTURER, update classes instead of deleting them
            if (account.getRole() != null && "LECTURER".equalsIgnoreCase(account.getRole().getName())) {
                try {
                    List<Classes> classesToUpdate = classesRepository.findAll().stream()
                            .filter(cls -> cls.getAccount() != null && cls.getAccount().getId().equals(accountId))
                            .toList();

                    // Update each class: set account to null and status to false (inactive)
                    for (Classes clazz : classesToUpdate) {
                        clazz.setAccount(null);  // Set lecturer to N/A
                        clazz.setStatus(false);  // Set status to Inactive
                        classesRepository.save(clazz);
                    }
                    classesRepository.flush();
                } catch (Exception e) {
                    System.err.println("Warning: Error updating classes for account " + accountId + ": " + e.getMessage());
                    throw new RuntimeException("Failed to update classes: " + e.getMessage(), e);
                }
            }

            // 4. Delete ClassAssignments (where account is student/lecturer)
            try {
                classAssignmentRepository.findAll().stream()
                        .filter(ca -> ca.getAccount() != null && ca.getAccount().getId().equals(accountId))
                        .forEach(classAssignmentRepository::delete);
                classAssignmentRepository.flush();
            } catch (Exception e) {
                System.err.println("Warning: Error deleting remaining class assignments for account " + accountId + ": " + e.getMessage());
                throw new RuntimeException("Failed to delete class assignments: " + e.getMessage(), e);
            }

            // 5. Delete PenaltyDetails first (before deleting Penalties)
            try {
                penaltyRepository.findPenaltiesByAccountId(accountId).forEach(penalty -> {
                    penaltyDetailRepository.findByPenaltyId(penalty.getId()).forEach(penaltyDetailRepository::delete);
                });
                penaltyDetailRepository.flush();
            } catch (Exception e) {
                System.err.println("Warning: Error deleting penalty details for account " + accountId + ": " + e.getMessage());
                throw new RuntimeException("Failed to delete penalty details: " + e.getMessage(), e);
            }

            // 6. Delete Penalties (using existing query method)
            try {
                penaltyRepository.findPenaltiesByAccountId(accountId).forEach(penaltyRepository::delete);
                penaltyRepository.flush();
            } catch (Exception e) {
                System.err.println("Warning: Error deleting penalties for account " + accountId + ": " + e.getMessage());
                throw new RuntimeException("Failed to delete penalties: " + e.getMessage(), e);
            }

            // 7. Delete BorrowingRequests (using existing query method)
            try {
                borrowingRequestRepository.findByRequestedById(accountId).forEach(borrowingRequestRepository::delete);
                borrowingRequestRepository.flush();
            } catch (Exception e) {
                System.err.println("Warning: Error deleting borrowing requests for account " + accountId + ": " + e.getMessage());
                throw new RuntimeException("Failed to delete borrowing requests: " + e.getMessage(), e);
            }

            // 8. Delete DamageReports (using native query to avoid enum mapping issues)
            try {
                damageReportRepository.deleteByGeneratedByAccountId(accountId);
            } catch (Exception e) {
                // Log error but continue with deletion (damage reports might not exist)
                System.err.println("Warning: Error deleting damage reports for account " + accountId + ": " + e.getMessage());
            }

            // 9. Delete Wallet transactions first (if wallet exists)
            if (account.getWallet() != null) {
                try {
                    walletTransactionRepository.findTransactionsByWalletId(account.getWallet().getId())
                            .forEach(walletTransactionRepository::delete);
                    walletTransactionRepository.flush();
                } catch (Exception e) {
                    System.err.println("Warning: Error deleting wallet transactions for account " + accountId + ": " + e.getMessage());
                    throw new RuntimeException("Failed to delete wallet transactions: " + e.getMessage(), e);
                }
            }

            // 10. Wallet will be deleted automatically due to cascade ALL, but we need to handle it explicitly
            // Delete wallet if it exists
            if (account.getWallet() != null) {
                try {
                    walletRepository.delete(account.getWallet());
                    walletRepository.flush();
                } catch (Exception e) {
                    System.err.println("Warning: Error deleting wallet for account " + accountId + ": " + e.getMessage());
                    throw new RuntimeException("Failed to delete wallet: " + e.getMessage(), e);
                }
            }

            // Finally, delete the account
            accountRepository.delete(account);
            accountRepository.flush(); // Force flush to ensure deletion

        } catch (ResourceNotFoundException e) {
            // Re-throw ResourceNotFoundException as-is
            throw e;
        } catch (RuntimeException e) {
            // Re-throw RuntimeException as-is
            throw e;
        } catch (Exception e) {
            // Wrap any other exception
            System.err.println("Error deleting account " + accountId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete account: " + e.getMessage(), e);
        }
    }
}
