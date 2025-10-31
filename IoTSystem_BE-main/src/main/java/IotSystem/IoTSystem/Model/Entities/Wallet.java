package IotSystem.IoTSystem.Model.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallet")
public class Wallet extends Base {


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
//    @Column(name = "wallet_id",  columnDefinition = "uuid")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    private BigDecimal balance;

    private String currency;
    private String note;

    private boolean isActive;


    @OneToOne
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;


}
