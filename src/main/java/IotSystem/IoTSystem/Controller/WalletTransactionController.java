package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import IotSystem.IoTSystem.Service.Implement.WalletTransactionServiceImpl;
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






    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
