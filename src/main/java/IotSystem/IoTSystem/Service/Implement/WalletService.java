package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import IotSystem.IoTSystem.Model.Mappers.WalletMapper;
import IotSystem.IoTSystem.Model.Response.WalletResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class WalletService implements IWalletService {
    @Autowired
    private AccountRepository accountRepository;

    // Lấy tài khoản hiện tại từ SecurityContext
    private Account getCurrentAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public WalletResponse getMyWallet() {
        Account account = getCurrentAccount();
        Wallet wallet = account.getWallet();

        if (wallet == null) {
            throw new RuntimeException("Tài khoản chưa có ví");
        }

        return WalletMapper.toResponse(wallet);
    }
}
