package IotSystem.IoTSystem.Model.Request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String studentCode;
    private String roles;
    private String phoneNumber;
    private String fullName;
}