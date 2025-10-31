package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.WalletTransaction;

import java.util.List;
import java.util.UUID;

public interface IWalletTransactionService {
    List<WalletTransaction> getAll();






    void delete(UUID id);
}
