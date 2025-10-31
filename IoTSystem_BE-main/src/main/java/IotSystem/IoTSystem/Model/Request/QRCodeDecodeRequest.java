package IotSystem.IoTSystem.Model.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeDecodeRequest {
    private String base64QRCode;
}
