package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import IotSystem.IoTSystem.Model.Mappers.AccountMapper;
import IotSystem.IoTSystem.Model.Mappers.ResponseRegisterMapper;
import IotSystem.IoTSystem.Model.Request.LoginRequest;
import IotSystem.IoTSystem.Model.Request.RegisterRequest;
import IotSystem.IoTSystem.Model.Entities.Roles;
import IotSystem.IoTSystem.Model.Request.UpdateAccountRequest;
import IotSystem.IoTSystem.Model.Response.ProfileResponse;
import IotSystem.IoTSystem.Model.Response.RegisterResponse;
import IotSystem.IoTSystem.Security.TokenProvider;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.RolesRepository;
import IotSystem.IoTSystem.Service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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


    //lấy user hiện tại của hệ thống
    private Account getCurrentAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
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
        account.setPhone(request.getPhoneNumber());
        account.setEmail(request.getUsername());
        account.setFullName(request.getFullName());

        Roles role = rolesRepository.findByName(request.getRoles().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Did not found the role: " + request.getRoles()));

        if(role.getName().equals("STUDENT")){
            account.setStudentCode(request.getStudentCode());
        }

        accountRepository.save(account);

        return ResponseRegisterMapper.toResponse(account);

    }

    @Override
    public ProfileResponse updateProfile(UpdateAccountRequest request) {
        Account account = getCurrentAccount();
        account.setFullName(request.getFullName());
        account.setAvatarUrl(request.getAvatarUrl());
        account.setPhone(request.getPhone());

        Account saved = accountRepository.save(account);

        return new ProfileResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getAvatarUrl(),
                saved.getPhone(),
                saved.getStudentCode(),
                saved.getRole().getName(),
                saved.getCreatedAt(),
                saved.getIsActive()
        );
    }
    @Override
    public ProfileResponse getProfile() {
        Account account = getCurrentAccount(); // lấy từ SecurityContext
        return AccountMapper.toProfileResponse(account);
    }

    @Override
    public Page<ProfileResponse> getAllAccounts(Pageable pageable) {
        Page<Account> accountsPage = accountRepository.findAll(pageable);

        return accountsPage.map(AccountMapper::toProfileResponse);
    }

    @Override
    public List<ProfileResponse> getAllStudent(){
        Roles role = rolesRepository.findByName("STUDENT").orElseThrow();
        List<Account> accounts = accountRepository.findByRole(role);

        return accounts.stream().map(AccountMapper::toProfileResponse).toList();
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
    public ProfileResponse createAStudent(RegisterRequest request) {
        Account account = new Account();
        if (accountRepository.existsByEmail(request.getUsername())) {
            throw new RuntimeException("Username already taken");
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
        return AccountMapper.toProfileResponse(account);
    }

    @Override
    public ProfileResponse createALecturer(RegisterRequest request) {
        Account account = new Account();
        if (accountRepository.existsByEmail(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }


        Roles role = rolesRepository.findByName("LECTURER").orElseThrow();


        account.setStudentCode(request.getStudentCode());
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
        return AccountMapper.toProfileResponse(account);
    }

}
