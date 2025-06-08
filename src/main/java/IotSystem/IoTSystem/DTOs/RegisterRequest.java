package IotSystem.IoTSystem.DTOs;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String username;
    private String password;
}
