package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.Wallet_Transaction_Status;
import IotSystem.IoTSystem.Model.Entities.Enum.Wallet_Transaction_Type;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import IotSystem.IoTSystem.Model.Request.VNPayPaymentRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.WalletRepository;
import IotSystem.IoTSystem.Repository.WalletTransactionRepository;

import IotSystem.IoTSystem.Service.Implement.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @PostMapping("/create-payment")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPayment(
            @RequestBody VNPayPaymentRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate order ID
            String orderId = UUID.randomUUID().toString();

            // Create pending transaction
            Wallet wallet = account.getWallet();
            if (wallet == null) {
                throw new RuntimeException("Wallet not found");
            }

            WalletTransaction transaction = new WalletTransaction();
            transaction.setAmount(request.getAmount().doubleValue());
            transaction.setTransactionType(Wallet_Transaction_Type.TOP_UP);
            transaction.setTransactionStatus(Wallet_Transaction_Status.PENDING);
            transaction.setDescription("Top-up via VNPay: " + request.getOrderInfo());
            transaction.setPaymentMethod("VNPay");
            transaction.setWallet(wallet);
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setUpdatedAt(LocalDateTime.now());
            transaction = walletTransactionRepository.save(transaction);

            // Update request with transaction ID
            request.setOrderId(orderId);

            // Create payment URL
            String paymentUrl = vnPayService.createPaymentUrl(request, httpServletRequest);
            
            // Log payment URL for debugging
            System.out.println("=== Payment URL Debug ===");
            System.out.println("Payment URL: " + paymentUrl);
            System.out.println("Order ID: " + orderId);
            System.out.println("Amount: " + request.getAmount());
            System.out.println("========================");

            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            response.put("orderId", orderId);
            response.put("transactionId", transaction.getId().toString());

            ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>();
            apiResponse.setStatus(HTTPStatus.Ok);
            apiResponse.setMessage("Payment URL created successfully");
            apiResponse.setData(response);

            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace
            ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to create payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<String> vnpayReturn(
            @RequestParam Map<String, String> params) {
        
        try {
            // Process callback
            Map<String, String> callbackResult = vnPayService.processCallback(params);
            
            String responseCode = params.get("vnp_ResponseCode");
            String orderId = params.get("vnp_TxnRef");
            String amount = params.get("vnp_Amount");
            
            if ("00".equals(responseCode) && "true".equals(callbackResult.get("success"))) {
                // Convert amount from cents to VND
                double amountVND = Double.parseDouble(amount) / 100.0;
                
                // Find and update transaction
                // Note: We'll update the most recent PENDING transaction
                List<WalletTransaction> pendingTransactions = walletTransactionRepository.findAll()
                    .stream()
                    .filter(t -> t.getTransactionStatus() == Wallet_Transaction_Status.PENDING)
                    .filter(t -> t.getTransactionType() == Wallet_Transaction_Type.TOP_UP)
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .limit(1)
                    .toList();
                
                if (!pendingTransactions.isEmpty()) {
                    WalletTransaction transaction = pendingTransactions.get(0);
                    transaction.setTransactionStatus(Wallet_Transaction_Status.COMPLETED);
                    transaction.setUpdatedAt(LocalDateTime.now());
                    walletTransactionRepository.save(transaction);
                    
                    // Update wallet balance
                    Wallet wallet = transaction.getWallet();
                    BigDecimal currentBalance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
                    BigDecimal newBalance = currentBalance.add(BigDecimal.valueOf(amountVND));
                    wallet.setBalance(newBalance);
                    walletRepository.save(wallet);
                }
                
                // Return HTML that redirects to success page
                String successHtml = "<!DOCTYPE html><html><head><title>Payment Success</title>" +
                    "<meta http-equiv='refresh' content='3;url=http://localhost:3000/'></head>" +
                    "<body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                    "<h1 style='color: green;'>✓ Thanh toán thành công!</h1>" +
                    "<p>Số tiền: " + String.format("%.0f", amountVND) + " VND</p>" +
                    "<p>Bạn sẽ được chuyển hướng về trang chủ...</p>" +
                    "</body></html>";
                
                return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8")
                    .body(successHtml);
            } else {
                // Payment failed
                String failedHtml = "<!DOCTYPE html><html><head><title>Payment Failed</title>" +
                    "<meta http-equiv='refresh' content='3;url=http://localhost:3000/'></head>" +
                    "<body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                    "<h1 style='color: red;'>✗ Thanh toán thất bại</h1>" +
                    "<p>Vui lòng thử lại sau.</p>" +
                    "<p>Bạn sẽ được chuyển hướng về trang chủ...</p>" +
                    "</body></html>";
                
                return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8")
                    .body(failedHtml);
            }
        } catch (Exception e) {
            String errorHtml = "<!DOCTYPE html><html><head><title>Payment Error</title>" +
                "<meta http-equiv='refresh' content='3;url=http://localhost:3000/'></head>" +
                "<body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                "<h1 style='color: red;'>✗ Lỗi xử lý thanh toán</h1>" +
                "<p>" + e.getMessage() + "</p>" +
                "<p>Bạn sẽ được chuyển hướng về trang chủ...</p>" +
                "</body></html>";
            
            return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8")
                .body(errorHtml);
        }
    }

    @PostMapping("/update-transaction")
    public ResponseEntity<ApiResponse<String>> updateTransaction(
            @RequestParam String transactionId,
            @RequestParam Wallet_Transaction_Status status,
            @RequestParam(required = false) Double amount) {
        try {
            UUID txId = UUID.fromString(transactionId);
            WalletTransaction transaction = walletTransactionRepository.findById(txId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            transaction.setTransactionStatus(status);
            
            if (status == Wallet_Transaction_Status.COMPLETED && amount != null) {
                // Update wallet balance
                Wallet wallet = transaction.getWallet();
                BigDecimal currentBalance = wallet.getBalance();
                BigDecimal newBalance = currentBalance.add(BigDecimal.valueOf(amount));
                wallet.setBalance(newBalance);
                walletRepository.save(wallet);
            }
            
            walletTransactionRepository.save(transaction);

            ApiResponse<String> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Transaction updated successfully");
            response.setData("Transaction UTID: " + transactionId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to update transaction: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
