package IotSystem.IoTSystem.Controller;


import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Request.LoginRequest;
import IotSystem.IoTSystem.Model.Request.RegisterRequest;
import IotSystem.IoTSystem.Model.Request.UpdateAccountRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Response.ProfileResponse;
import IotSystem.IoTSystem.Service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class AccountController {
    @Autowired
    private IAccountService accountService;

    // Endpoint đăng nhập
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = accountService.login(loginRequest);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Login failed: " + e.getMessage());
        }
    }

    // Endpoint đăng ký
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        try {
            String result = accountService.register(registerRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Register failed: " + e.getMessage());
        }
    }
    @GetMapping("/me")
    public String getCurrentUser(Authentication authentication) {
        return "User: " + authentication.getName() +
                " - Roles: " + authentication.getAuthorities();
    }
    //update profile ca nhan
    // Update profile của chính user đang đăng nhập
    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @RequestBody UpdateAccountRequest request) {

        ProfileResponse updated = accountService.updateProfile(request);

        ApiResponse<ProfileResponse> response = new ApiResponse<>();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Profile updated successfully");
        response.setData(updated);

        return ResponseEntity.ok(response);
    }

}

