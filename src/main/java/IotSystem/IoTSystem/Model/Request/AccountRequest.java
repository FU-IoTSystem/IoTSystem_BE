package IotSystem.IoTSystem.Model.Request;

import IotSystem.IoTSystem.Model.Entities.Roles;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
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
