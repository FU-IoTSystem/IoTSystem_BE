package IotSystem.IoTSystem.Controller;


import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Response.WalletResponse;
import IotSystem.IoTSystem.Service.IWalletService;
import IotSystem.IoTSystem.Service.Implement.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    IWalletService walletService;

    @GetMapping("/myWallet")
    public ResponseEntity<ApiResponse<WalletResponse>> getMyWallet() {
        WalletResponse wallet = walletService.getMyWallet(); // ✅ gọi đúng instance

        ApiResponse<WalletResponse> response = new ApiResponse<>();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Fetched wallet successfully");
        response.setData(wallet);

        return ResponseEntity.ok(response);
    }

}
