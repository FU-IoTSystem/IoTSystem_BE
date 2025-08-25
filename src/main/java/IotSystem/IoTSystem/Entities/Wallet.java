package IotSystem.IoTSystem.Entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.math.BigDecimal;

@Entity
public class Wallet {
    @Id
    Long id;
    @OneToOne
    Account owner;
    BigDecimal balance;
}
