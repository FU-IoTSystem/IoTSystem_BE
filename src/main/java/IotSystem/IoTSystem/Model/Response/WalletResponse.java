package IotSystem.IoTSystem.Model.Response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter


public class WalletResponse {
    private UUID walletId;
    private BigDecimal balance;
    private String currency;
    private String note;
    private boolean isActive;
    public WalletResponse(UUID walletId, BigDecimal balance, String currency, String note, boolean isActive) {
        this.walletId = walletId;
        this.balance = balance;
        this.currency = currency;
        this.note = note;
        this.isActive = isActive;
    }

}
