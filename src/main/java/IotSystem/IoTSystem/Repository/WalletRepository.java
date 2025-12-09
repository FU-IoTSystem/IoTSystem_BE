package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WalletRepository  extends JpaRepository<Wallet, UUID> {
//    Wallet findByAccountId(UUID accountID);
}
