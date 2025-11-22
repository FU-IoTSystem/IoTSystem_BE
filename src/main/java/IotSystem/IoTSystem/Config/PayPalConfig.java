package IotSystem.IoTSystem.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class PayPalConfig implements InitializingBean {
    
    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode:sandbox}")
    private String mode; // sandbox or live

    @Value("${paypal.currency:USD}")
    private String currency; // e.g., USD

    @Value("${paypal.return.url:http://localhost:3000/member}")
    private String returnUrl;

    @Value("${paypal.cancel.url:http://localhost:3000/member}")
    private String cancelUrl;

    @Value("${paypal.exchange-rate:24000.0}")
    private double exchangeRate; // 1 USD = 24,000 VND

    @Override
    public void afterPropertiesSet() {
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalStateException("PayPal client ID is not configured");
        }
        if (clientSecret == null || clientSecret.isEmpty()) {
            throw new IllegalStateException("PayPal client secret is not configured");
        }
        System.out.println("=== PayPal Configuration ===");
        System.out.println("Client ID: " + (clientId != null ? clientId.substring(0, Math.min(10, clientId.length())) + "..." : "NULL"));
        System.out.println("Client Secret: " + (clientSecret != null ? "***" : "NULL"));
        System.out.println("Mode: " + mode);
        System.out.println("Currency: " + currency);
        System.out.println("Return URL: " + returnUrl);
        System.out.println("Cancel URL: " + cancelUrl);
        System.out.println("Exchange Rate: " + exchangeRate);
        System.out.println("===========================");
    }
}

