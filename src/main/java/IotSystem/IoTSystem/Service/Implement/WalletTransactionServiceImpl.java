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

    public WalletTransaction getById(UUID id) {
        return walletTransactionRepository.getById(id);
    }

    public List<WalletTransaction> getByAccount(UUID accountId) {
        return walletTransactionRepository.findByAccountId(accountId);
    }

    public WalletTransaction create(WalletTransaction transaction) {
        transaction.setCreatedAt(new Date());
        return walletTransactionRepository.save(transaction);
    }

    public WalletTransaction update(UUID id, WalletTransaction updated) {
        WalletTransaction existing = walletTransactionRepository.getById(id);
        if (existing != null) {
            existing.setAmount(updated.getAmount());
            existing.setType(updated.getType());
            existing.setDescription(updated.getDescription());
            existing.setPaymentMethod(updated.getPaymentMethod());
            existing.setTransactionStatus(updated.getTransactionStatus());
            existing.setAccount(updated.getAccount());
            existing.setCreatedAt(updated.getCreatedAt());
            return walletTransactionRepository.save(existing);
        }
        return null;
    }

    public void delete(UUID id) {
        walletTransactionRepository.deleteById(id);
    }
}
