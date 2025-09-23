package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import IotSystem.IoTSystem.Repository.WalletTransactionRepository;
import IotSystem.IoTSystem.Service.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service

public class WalletTransactionServiceImpl implements WalletTransactionService {
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    public List<WalletTransaction> getAll() {
        return walletTransactionRepository.findAll();
    }






    public void delete(UUID id) {
        walletTransactionRepository.deleteById(id);
    }
}
