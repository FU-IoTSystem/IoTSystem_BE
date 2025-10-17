package IotSystem.IoTSystem.Model.Response;

import lombok.Data;

@Data
public class ResetPasswordResponse {
    private String token;
    private String newPassword;

}
