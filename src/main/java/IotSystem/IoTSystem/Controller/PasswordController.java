package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Request.ChangePasswordRequest;
import IotSystem.IoTSystem.Model.Request.ForgotPasswordRequest;
import IotSystem.IoTSystem.Model.Response.ResetPasswordResponse;
import IotSystem.IoTSystem.Service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/passwords")
public class PasswordController {

    @Autowired
    private IAccountService accountService;


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        accountService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }

}
