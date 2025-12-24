package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import IotSystem.IoTSystem.Model.Request.TopUpRequest;
import IotSystem.IoTSystem.Model.Request.TransferRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Response.TransactionHistoryResponse;
import IotSystem.IoTSystem.Service.IWalletTransactionService;
import IotSystem.IoTSystem.Service.Implement.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet-transactions")
public class WalletTransactionController {

    @Autowired
    private IWalletTransactionService service;

    @Autowired
    private NotificationServiceImpl notificationService;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<TransactionHistoryResponse>>> getAll() {
        List<TransactionHistoryResponse> transactions = service.getAll();

        ApiResponse<List<TransactionHistoryResponse>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched wallet transactions successfully");
        response.setData(transactions);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/top-up")
    public ResponseEntity<ApiResponse<WalletTransaction>> topUp(@RequestBody TopUpRequest request) {
        try {
            // Validate amount
            if (request.getAmount() == null || request.getAmount() < 10000) {
                ApiResponse<WalletTransaction> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("Amount must be at least 10,000 VND");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            if (request.getAmount() > 10000000) {
                ApiResponse<WalletTransaction> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("Amount must not exceed 10,000,000 VND");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Create top-up transaction
            WalletTransaction transaction = service.createTopUp(request.getAmount(), request.getDescription());

            ApiResponse<WalletTransaction> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Top-up successful");
            response.setData(transaction);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<WalletTransaction> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to process top-up: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<WalletTransaction>> transfer(@RequestBody TransferRequest request) {
        try {
            // Validate request
            if (request.getRecipientEmail() == null || request.getRecipientEmail().trim().isEmpty()) {
                ApiResponse<WalletTransaction> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("Recipient email is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Validate amount
            if (request.getAmount() == null || request.getAmount() < 10000) {
                ApiResponse<WalletTransaction> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("Amount must be at least 10,000 VND");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Create transfer transaction
            WalletTransaction transaction = service.transfer(
                    request.getRecipientEmail(),
                    request.getAmount(),
                    request.getDescription()
            );

            ApiResponse<WalletTransaction> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Transfer successful");
            response.setData(transaction);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<WalletTransaction> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.BadRequest);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ApiResponse<WalletTransaction> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to process transfer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TransactionHistoryResponse>>> getTransactionHistory() {
        try {
            List<TransactionHistoryResponse> history = service.getTransactionHistory();

            ApiResponse<List<TransactionHistoryResponse>> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Fetched transaction history successfully");
            response.setData(history);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<TransactionHistoryResponse>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to fetch transaction history: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
