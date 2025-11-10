package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import IotSystem.IoTSystem.Model.Response.TransactionHistoryResponse;

import java.util.List;
import java.util.UUID;

public interface IWalletTransactionService {
    List<TransactionHistoryResponse> getAll();

    WalletTransaction createTopUp(Double amount, String description);

    void delete(UUID id);

    List<TransactionHistoryResponse> getTransactionHistory();
}
