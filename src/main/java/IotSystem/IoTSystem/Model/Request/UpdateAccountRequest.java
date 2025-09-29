package IotSystem.IoTSystem.Model.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAccountRequest {
    private String fullName;
    private String avatarUrl;
    private String phone;
}
