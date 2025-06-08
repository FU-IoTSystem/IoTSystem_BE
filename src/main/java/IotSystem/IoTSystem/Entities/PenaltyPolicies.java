package IotSystem.IoTSystem.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "penalty_policies")
public class PenaltyPolicies {
    @Id
    private Integer id;

    private String semester;
    private String courseName;
    private BigDecimal depositAmount;
    private BigDecimal penaltyPerDay;
    private BigDecimal damagedPenalty;
}

