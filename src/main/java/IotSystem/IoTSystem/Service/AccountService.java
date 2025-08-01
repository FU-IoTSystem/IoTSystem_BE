package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.DTOs.LoginRequest;
import IotSystem.IoTSystem.DTOs.RegisterRequest;
import IotSystem.IoTSystem.Entities.Account;
import IotSystem.IoTSystem.Entities.Roles;
import IotSystem.IoTSystem.Jwt.TokenProvider;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
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

        // Lấy role làm quyền truy cập (dạng ROLE_USER, ROLE_ADMIN...)
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + account.getRole().getName()));

        // Tạo đối tượng UserDetails
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPassword(),
                authorities
        );

        // Sinh token JWT
        return tokenProvider.generateToken(userDetails);
    }
    public String register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Gán role mặc định là "USER"
        Roles role = rolesRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));

        Account account = new Account();
        account.setEmail(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        account.setIsActive(true);
        account.setRole(role);

        accountRepository.save(account);
        return "Register successful";
    }


}
