    package IotSystem.IoTSystem.Controller;


    import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
    import IotSystem.IoTSystem.Model.Request.LoginRequest;
    import IotSystem.IoTSystem.Model.Request.RegisterRequest;
    import IotSystem.IoTSystem.Model.Request.UpdateAccountRequest;
    import IotSystem.IoTSystem.Model.Response.ApiResponse;
    import IotSystem.IoTSystem.Model.Response.ProfileResponse;
    import IotSystem.IoTSystem.Model.Response.RegisterResponse;
    import IotSystem.IoTSystem.Service.IAccountService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.Authentication;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.UUID;

    @RestController
    @CrossOrigin("*")
    @RequestMapping("/api")
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
        public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
            try {
                RegisterResponse result = accountService.register(registerRequest);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Register failed: " + e.getMessage());
            }
        }

        @PutMapping("/register/{id}")
        public ResponseEntity<RegisterResponse> updateAccount(@PathVariable UUID id, @RequestBody RegisterRequest request){
            RegisterResponse response = accountService.updating(request, id);
            return ResponseEntity.ok(response);
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
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Profile updated successfully");
            response.setData(updated);

            return ResponseEntity.ok(response);
        }
        //get profile for all ueser
        @GetMapping("/me/profile")
        public ResponseEntity<ApiResponse<ProfileResponse>> getProfile() {
            ProfileResponse profile = accountService.getProfile();

            ApiResponse<ProfileResponse> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Profile fetched successfully");
            response.setData(profile);

            return ResponseEntity.ok(response);
        }

        @GetMapping("/admin/accounts")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<Page<ProfileResponse>>> getAllAccounts(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size) {

            Page<ProfileResponse> profiles = accountService.getAllAccounts(PageRequest.of(page, size));

            ApiResponse<Page<ProfileResponse>> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Fetched accounts with pagination");
            response.setData(profiles);

            return ResponseEntity.ok(response);
        }

        @GetMapping("/admin/accounts/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<ProfileResponse>> getAccountById(@PathVariable UUID id) {
            ProfileResponse profile = accountService.getAccountById(id);

            ApiResponse<ProfileResponse> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Fetched account successfully");
            response.setData(profile);

            return ResponseEntity.ok(response);
        }
        @PostMapping("/aas/create-single-student")
        public ResponseEntity<ApiResponse<ProfileResponse>> createStudent(@RequestBody RegisterRequest request){
            ProfileResponse profileResponse = accountService.createAStudent(request);

            ApiResponse<ProfileResponse> response = new ApiResponse<>();

            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Fetched account successfully");
            response.setData(profileResponse);

            return ResponseEntity.ok(response);
        }

        @PostMapping("/aas/create-single-lecturer")
        public ResponseEntity<ApiResponse<ProfileResponse>> createLecturer(@RequestBody RegisterRequest request){
            ProfileResponse profileResponse = accountService.createALecturer(request);

            ApiResponse<ProfileResponse> response = new ApiResponse<>();

            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Fetched account successfully");
            response.setData(profileResponse);

            return ResponseEntity.ok(response);
        }


        @GetMapping("/getAllStudent")
        public ResponseEntity<ApiResponse<List<ProfileResponse>>> getAllStudent(){
            List<ProfileResponse> profileResponses = accountService.getAllStudent();

            ApiResponse<List<ProfileResponse>> response = new ApiResponse<>();

            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Fetched account successfully");
            response.setData(profileResponses);

            return ResponseEntity.ok(response);
        }
    }

