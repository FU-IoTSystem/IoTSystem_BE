package IotSystem.IoTSystem.Model.Response;

import lombok.Data;

@Data
public class ResetPasswordResponse {
    private String email;
    private String otp;
    private String token;
    private String newPassword;

}
