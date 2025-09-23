package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.StudentGroup;
import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {

    List<WalletTransaction> findAll();

    WalletTransaction getById(UUID id);



    WalletTransaction save(WalletTransaction transaction);

    void deleteById(UUID id);
}
