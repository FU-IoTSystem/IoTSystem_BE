package IotSystem.IoTSystem.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String avatarUrl;
    private String phone;
    private String studentCode;
    private String role;
}
