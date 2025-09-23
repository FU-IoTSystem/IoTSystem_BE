package IotSystem.IoTSystem.Model.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "penalty_policies")
public class PenaltyPolicies {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String type;
    private Double amount;
    private Date issuedDate;
    private Date resolved;


    @ManyToOne
    @JoinColumn(name = "penalty_id")
    private Penalty penalty;
}

