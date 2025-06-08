package IotSystem.IoTSystem.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;
    private String fullName;


    @Column(name = "email", unique = true, nullable = false)
    private String email;
    // email dùng để đăng nhập, unique , ko đc null

    private String phone;



    @ManyToOne(targetEntity = Roles.class)
    private Roles role;


    private BigDecimal walletBalance;
    private String password;
    private Boolean isActive;
}
