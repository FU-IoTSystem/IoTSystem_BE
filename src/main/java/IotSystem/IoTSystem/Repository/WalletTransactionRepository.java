package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletTransactionRepository {

    List<WalletTransaction> findAll();

    WalletTransaction getById(UUID id);

    List<WalletTransaction> findByAccountId(UUID accountId);

    WalletTransaction save(WalletTransaction transaction);

    void deleteById(UUID id);
}
