package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Entities.WalletTransaction;
import IotSystem.IoTSystem.Service.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet-transactions")
public class WalletTransactionController {

    @Autowired
    private WalletTransactionService service;

    @GetMapping("/getAll")
    public List<WalletTransaction> getAll() {
        return service.getAll();
    }

    @GetMapping("/getById/{id}")
    public WalletTransaction getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/account/{accountId}")
    public List<WalletTransaction> getByAccount(@PathVariable UUID accountId) {
        return service.getByAccount(accountId);
    }

    @PostMapping("/create")
    public WalletTransaction create(@RequestBody WalletTransaction transaction) {
        return service.create(transaction);
    }

    @PutMapping("/update/{id}")
    public WalletTransaction update(@PathVariable UUID id, @RequestBody WalletTransaction transaction) {
        return service.update(id, transaction);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
