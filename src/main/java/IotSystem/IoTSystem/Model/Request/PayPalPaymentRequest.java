package IotSystem.IoTSystem.Model.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayPalPaymentRequest {
    private BigDecimal amount;
    private String orderId;
    private String description;
    private String currency = "USD";
}

