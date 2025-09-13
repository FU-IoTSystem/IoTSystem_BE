package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Entities.WalletTransaction;
import IotSystem.IoTSystem.Repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service

public class WalletTransactionService {
    @Autowired
    private WalletTransactionRepository repository;

    public List<WalletTransaction> getAll() {
        return repository.findAll();
    }

    public WalletTransaction getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public List<WalletTransaction> getByAccount(UUID accountId) {
        return repository.findByAccountId(accountId);
    }

    public WalletTransaction create(WalletTransaction transaction) {
        transaction.setCreatedAt(new Date());
        return repository.save(transaction);
    }

    public WalletTransaction update(UUID id, WalletTransaction updated) {
        WalletTransaction existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setAmount(updated.getAmount());
            existing.setType(updated.getType());
            existing.setDescription(updated.getDescription());
            existing.setPaymentMethod(updated.getPaymentMethod());
            existing.setTransactionStatus(updated.getTransactionStatus());
            existing.setAccount(updated.getAccount());
            existing.setCreatedAt(updated.getCreatedAt());
            return repository.save(existing);
        }
        return null;
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
