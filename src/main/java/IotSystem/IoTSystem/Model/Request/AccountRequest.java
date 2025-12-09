package IotSystem.IoTSystem.Model.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    private String fullName;
    private String phone;
    private String email;
    private String studentCode;
    private String lecturerCode;
    private String password;
    private String role;
    private Boolean isActive;
}
