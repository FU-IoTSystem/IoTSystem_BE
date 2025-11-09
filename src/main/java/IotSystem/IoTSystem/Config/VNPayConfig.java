package IotSystem.IoTSystem.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vnpay")
@Getter
@Setter
public class VNPayConfig {
    private String version = "2.1.0";
    private String command = "pay";
    private String tmnCode = "RAO2UNH7"; // Sandbox TMN Code
    private String secretKey = "ZQYTHFZPWSIGPMSQYZPFPBWHQJYKZOVL"; // Sandbox Secret Key
    private String vnpPayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private String vnpReturnUrl = "http://localhost:3000/vnpay-return";
    private String vnpApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    private String orderType = "other";
    private String locale = "vn";
    private String currencyCode = "VND";
    private int defaultTimeout = 15 * 60; // 15 minutes
}

