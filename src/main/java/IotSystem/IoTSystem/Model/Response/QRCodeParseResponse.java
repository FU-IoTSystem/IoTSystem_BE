package IotSystem.IoTSystem.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeParseResponse {
    private String type; // BORROWING_REQUEST or COMPONENT_RENTAL
    private Map<String, String> data; // Parsed key-value pairs from QR text
}
