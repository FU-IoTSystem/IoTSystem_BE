package IotSystem.IoTSystem.Model.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VNPayPaymentRequest {
    private Long amount;
    private String orderId;
    private String orderInfo;
    private String bankCode;
}

