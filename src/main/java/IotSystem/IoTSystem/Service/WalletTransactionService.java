package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.WalletTransaction;

import java.util.List;
import java.util.UUID;

public interface WalletTransactionService {
    List<WalletTransaction> getAll();

    WalletTransaction getById(UUID id);

    List<WalletTransaction> getByAccount(UUID accountId);

    WalletTransaction create(WalletTransaction transaction);

    WalletTransaction update(UUID id, WalletTransaction transaction);

    void delete(UUID id);
}
