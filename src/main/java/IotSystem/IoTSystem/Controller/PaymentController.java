package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.Wallet_Transaction_Status;
import IotSystem.IoTSystem.Model.Entities.Enum.Wallet_Transaction_Type;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import IotSystem.IoTSystem.Model.Request.PayPalPaymentRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.WalletRepository;
import IotSystem.IoTSystem.Repository.WalletTransactionRepository;
import IotSystem.IoTSystem.Service.PayPalService;
import IotSystem.IoTSystem.Config.PayPalConfig;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private PayPalConfig payPalConfig;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @PostMapping("/paypal/create")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPayPalPayment(
            @RequestBody Map<String, Object> requestBody) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Extract amount (USD), description, and returnUrl from request
            BigDecimal amountUSD = new BigDecimal(requestBody.get("amount").toString());
            String description = requestBody.get("description") != null
                    ? requestBody.get("description").toString()
                    : "Top-up IoT Wallet";
            String returnUrl = requestBody.get("returnUrl") != null
                    ? requestBody.get("returnUrl").toString()
                    : payPalConfig.getReturnUrl();
            String cancelUrl = requestBody.get("cancelUrl") != null
                    ? requestBody.get("cancelUrl").toString()
                    : payPalConfig.getCancelUrl();

            // Convert USD to VND for wallet balance
            BigDecimal amountVND = amountUSD.multiply(BigDecimal.valueOf(payPalConfig.getExchangeRate()));

            // Generate order ID
            String orderId = UUID.randomUUID().toString();

            // Create pending transaction
            Wallet wallet = account.getWallet();
            if (wallet == null) {
                throw new RuntimeException("Wallet not found");
            }

            // Get current balance before transaction
            BigDecimal currentBalance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
            Double previousBalance = currentBalance.doubleValue();

            WalletTransaction transaction = new WalletTransaction();
            // Store VND amount in transaction (for wallet balance update)
            transaction.setAmount(amountVND.doubleValue());
            transaction.setPreviousBalance(previousBalance);
            transaction.setTransactionType(Wallet_Transaction_Type.TOP_UP);
            transaction.setTransactionStatus(Wallet_Transaction_Status.PENDING);
            transaction.setDescription("Top-up via PayPal: " + description + " (USD: $" + amountUSD + ")");
            transaction.setPaymentMethod("PayPal");
            transaction.setWallet(wallet);
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setUpdatedAt(LocalDateTime.now());
            transaction = walletTransactionRepository.save(transaction);

            // Create PayPal payment request (use USD amount)
            PayPalPaymentRequest payPalRequest = new PayPalPaymentRequest();
            payPalRequest.setAmount(amountUSD);
            payPalRequest.setOrderId(orderId);
            payPalRequest.setDescription(description);
            payPalRequest.setCurrency(payPalConfig.getCurrency()); // Set currency from config

            // Create PayPal payment with custom return/cancel URLs
            Payment payment = payPalService.createPayment(payPalRequest, returnUrl, cancelUrl);
            String approvalUrl = payPalService.getApprovalUrl(payment);

            Map<String, String> response = new HashMap<>();
            response.put("paymentId", payment.getId());
            response.put("approvalUrl", approvalUrl);
            response.put("orderId", orderId);
            response.put("transactionId", transaction.getId().toString());

            ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>();
            apiResponse.setStatus(IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus.Ok);
            apiResponse.setMessage("PayPal payment created successfully");
            apiResponse.setData(response);

            return ResponseEntity.ok(apiResponse);
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to create PayPal payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to create payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/paypal/execute")
    public ResponseEntity<ApiResponse<Map<String, String>>> executePayPalPayment(
            @RequestParam String paymentId,
            @RequestParam String payerId,
            @RequestParam(required = false) String transactionId) {
        try {
            // Check payment state first before executing
            Payment existingPayment = null;
            try {
                existingPayment = payPalService.getPaymentDetails(paymentId);
                // If payment is already approved, skip execution
                if (existingPayment != null && "approved".equalsIgnoreCase(existingPayment.getState())) {
                    System.out.println("Payment " + paymentId + " is already approved. Skipping execution.");
                    // Use existing payment instead of executing again
                    Payment payment = existingPayment;

                    // Get transaction amount from payment (USD)
                    BigDecimal amountUSD = new BigDecimal(payment.getTransactions().get(0).getAmount().getTotal());

                    // Find and update transaction if not already completed
                    WalletTransaction transaction = null;
                    if (transactionId != null && !transactionId.isEmpty()) {
                        UUID txId = UUID.fromString(transactionId);
                        transaction = walletTransactionRepository.findById(txId)
                                .orElse(null);
                    } else {
                        // Find most recent PENDING transaction
                        List<WalletTransaction> pendingTransactions = walletTransactionRepository.findAll()
                                .stream()
                                .filter(t -> t.getTransactionStatus() == Wallet_Transaction_Status.PENDING)
                                .filter(t -> t.getTransactionType() == Wallet_Transaction_Type.TOP_UP)
                                .filter(t -> "PayPal".equals(t.getPaymentMethod()))
                                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                                .limit(1)
                                .toList();

                        if (!pendingTransactions.isEmpty()) {
                            transaction = pendingTransactions.get(0);
                        }
                    }

                    // Update transaction if found and still pending
                    if (transaction != null && transaction.getTransactionStatus() == Wallet_Transaction_Status.PENDING) {
                        transaction.setTransactionStatus(Wallet_Transaction_Status.COMPLETED);
                        transaction.setUpdatedAt(LocalDateTime.now());
                        walletTransactionRepository.save(transaction);

                        // Update wallet balance using transaction amount (already in VND)
                        Wallet wallet = transaction.getWallet();
                        BigDecimal currentBalance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
                        BigDecimal transactionAmountVND = BigDecimal.valueOf(transaction.getAmount());
                        BigDecimal newBalance = currentBalance.add(transactionAmountVND);
                        wallet.setBalance(newBalance);
                        walletRepository.save(wallet);
                    }

                    Map<String, String> response = new HashMap<>();
                    response.put("paymentId", payment.getId());
                    response.put("state", payment.getState());
                    response.put("amountUSD", amountUSD.toString());
                    response.put("message", "Payment was already completed");
                    if (transaction != null) {
                        response.put("transactionId", transaction.getId().toString());
                        response.put("amountVND", String.valueOf(transaction.getAmount()));
                    }

                    ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>();
                    apiResponse.setStatus(IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus.Ok);
                    apiResponse.setMessage("Payment was already completed successfully");
                    apiResponse.setData(response);

                    return ResponseEntity.ok(apiResponse);
                }
            } catch (PayPalRESTException e) {
                // If we can't get payment details, continue with execution attempt
                System.out.println("Could not get payment details, will attempt execution: " + e.getMessage());
            }

            // Execute PayPal payment (only if not already approved)
            Payment payment = null;
            try {
                payment = payPalService.executePayment(paymentId, payerId);
            } catch (PayPalRESTException e) {
                // Handle PAYMENT_ALREADY_DONE error - payment was already executed
                if (e.getMessage() != null && e.getMessage().contains("PAYMENT_ALREADY_DONE")) {
                    System.out.println("Payment already executed. Getting payment details instead.");
                    try {
                        payment = payPalService.getPaymentDetails(paymentId);
                        if (payment != null && "approved".equalsIgnoreCase(payment.getState())) {
                            System.out.println("Payment is already approved. Proceeding with transaction update.");
                        } else {
                            throw new RuntimeException("Payment state is not approved: " + (payment != null ? payment.getState() : "null"));
                        }
                    } catch (PayPalRESTException ex) {
                        throw new RuntimeException("Failed to get payment details after PAYMENT_ALREADY_DONE: " + ex.getMessage());
                    }
                } else {
                    // Re-throw if it's a different error
                    throw e;
                }
            }

            // Check if payment is approved
            if (payment == null || !"approved".equalsIgnoreCase(payment.getState())) {
                throw new RuntimeException("Payment not approved. State: " + (payment != null ? payment.getState() : "null"));
            }

            // Get transaction amount from payment (USD)
            BigDecimal amountUSD = new BigDecimal(payment.getTransactions().get(0).getAmount().getTotal());

            // Find and update transaction
            WalletTransaction transaction = null;
            if (transactionId != null && !transactionId.isEmpty()) {
                UUID txId = UUID.fromString(transactionId);
                transaction = walletTransactionRepository.findById(txId)
                        .orElseThrow(() -> new RuntimeException("Transaction not found"));
            } else {
                // Find most recent PENDING transaction
                List<WalletTransaction> pendingTransactions = walletTransactionRepository.findAll()
                        .stream()
                        .filter(t -> t.getTransactionStatus() == Wallet_Transaction_Status.PENDING)
                        .filter(t -> t.getTransactionType() == Wallet_Transaction_Type.TOP_UP)
                        .filter(t -> "PayPal".equals(t.getPaymentMethod()))
                        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                        .limit(1)
                        .toList();

                if (!pendingTransactions.isEmpty()) {
                    transaction = pendingTransactions.get(0);
                }
            }

            if (transaction != null) {
                // Only update if transaction is still pending (avoid duplicate updates)
                if (transaction.getTransactionStatus() == Wallet_Transaction_Status.PENDING) {
                    // Update transaction status
                    transaction.setTransactionStatus(Wallet_Transaction_Status.COMPLETED);
                    transaction.setUpdatedAt(LocalDateTime.now());
                    walletTransactionRepository.save(transaction);

                    // Update wallet balance using transaction amount (already in VND)
                    Wallet wallet = transaction.getWallet();
                    BigDecimal currentBalance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
                    BigDecimal transactionAmountVND = BigDecimal.valueOf(transaction.getAmount());
                    BigDecimal newBalance = currentBalance.add(transactionAmountVND);
                    wallet.setBalance(newBalance);
                    walletRepository.save(wallet);
                } else {
                    System.out.println("Transaction " + transaction.getId() + " is already completed. Skipping wallet update.");
                }
            }

            Map<String, String> response = new HashMap<>();
            response.put("paymentId", payment.getId());
            response.put("state", payment.getState());
            response.put("amountUSD", amountUSD.toString());
            if (transaction != null) {
                response.put("transactionId", transaction.getId().toString());
                response.put("amountVND", String.valueOf(transaction.getAmount()));
            }

            ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>();
            apiResponse.setStatus(IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus.Ok);
            apiResponse.setMessage("PayPal payment executed successfully");
            apiResponse.setData(response);

            return ResponseEntity.ok(apiResponse);
        } catch (PayPalRESTException e) {
            e.printStackTrace();

            // Handle PAYMENT_ALREADY_DONE error gracefully
            if (e.getMessage() != null && e.getMessage().contains("PAYMENT_ALREADY_DONE")) {
                System.out.println("Payment already done. Attempting to get payment details and update transaction.");

                try {
                    // Get payment details to verify it's approved
                    Payment payment = payPalService.getPaymentDetails(paymentId);
                    if (payment != null && "approved".equalsIgnoreCase(payment.getState())) {
                        // Payment is already approved, update transaction if needed
                        BigDecimal amountUSD = new BigDecimal(payment.getTransactions().get(0).getAmount().getTotal());

                        WalletTransaction transaction = null;
                        if (transactionId != null && !transactionId.isEmpty()) {
                            UUID txId = UUID.fromString(transactionId);
                            transaction = walletTransactionRepository.findById(txId).orElse(null);
                        } else {
                            // Find most recent PENDING transaction
                            List<WalletTransaction> pendingTransactions = walletTransactionRepository.findAll()
                                    .stream()
                                    .filter(t -> t.getTransactionStatus() == Wallet_Transaction_Status.PENDING)
                                    .filter(t -> t.getTransactionType() == Wallet_Transaction_Type.TOP_UP)
                                    .filter(t -> "PayPal".equals(t.getPaymentMethod()))
                                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                                    .limit(1)
                                    .toList();

                            if (!pendingTransactions.isEmpty()) {
                                transaction = pendingTransactions.get(0);
                            }
                        }

                        // Update transaction if found and still pending
                        if (transaction != null && transaction.getTransactionStatus() == Wallet_Transaction_Status.PENDING) {
                            transaction.setTransactionStatus(Wallet_Transaction_Status.COMPLETED);
                            transaction.setUpdatedAt(LocalDateTime.now());
                            walletTransactionRepository.save(transaction);

                            // Update wallet balance
                            Wallet wallet = transaction.getWallet();
                            BigDecimal currentBalance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
                            BigDecimal transactionAmountVND = BigDecimal.valueOf(transaction.getAmount());
                            BigDecimal newBalance = currentBalance.add(transactionAmountVND);
                            wallet.setBalance(newBalance);
                            walletRepository.save(wallet);
                        }

                        Map<String, String> response = new HashMap<>();
                        response.put("paymentId", payment.getId());
                        response.put("state", payment.getState());
                        response.put("amountUSD", amountUSD.toString());
                        response.put("message", "Payment was already completed");
                        if (transaction != null) {
                            response.put("transactionId", transaction.getId().toString());
                            response.put("amountVND", String.valueOf(transaction.getAmount()));
                        }

                        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>();
                        apiResponse.setStatus(IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus.Ok);
                        apiResponse.setMessage("Payment was already completed successfully");
                        apiResponse.setData(response);

                        return ResponseEntity.ok(apiResponse);
                    }
                } catch (Exception ex) {
                    System.out.println("Error handling PAYMENT_ALREADY_DONE: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

            ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to execute PayPal payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to execute payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/paypal/return")
    public ResponseEntity<String> paypalReturn(
            @RequestParam(required = false) String paymentId,
            @RequestParam(required = false) String PayerID,
            @RequestParam(required = false) String orderId) {

        try {
            if (paymentId == null || PayerID == null) {
                throw new RuntimeException("Missing paymentId or PayerID");
            }

            // Check payment state first before executing
            Payment payment = null;
            try {
                Payment existingPayment = payPalService.getPaymentDetails(paymentId);
                // If payment is already approved, skip execution
                if (existingPayment != null && "approved".equalsIgnoreCase(existingPayment.getState())) {
                    System.out.println("Payment " + paymentId + " is already approved. Skipping execution.");
                    payment = existingPayment;
                } else {
                    // Execute payment only if not already approved
                    payment = payPalService.executePayment(paymentId, PayerID);
                }
            } catch (PayPalRESTException e) {
                // Handle PAYMENT_ALREADY_DONE error
                if (e.getMessage() != null && e.getMessage().contains("PAYMENT_ALREADY_DONE")) {
                    System.out.println("Payment already done. Getting payment details.");
                    payment = payPalService.getPaymentDetails(paymentId);
                } else {
                    throw e;
                }
            }

            if (payment != null && "approved".equalsIgnoreCase(payment.getState())) {
                // Get transaction amount (USD) from PayPal
                BigDecimal amountUSD = new BigDecimal(payment.getTransactions().get(0).getAmount().getTotal());

                // Find and update transaction
                List<WalletTransaction> pendingTransactions = walletTransactionRepository.findAll()
                        .stream()
                        .filter(t -> t.getTransactionStatus() == Wallet_Transaction_Status.PENDING)
                        .filter(t -> t.getTransactionType() == Wallet_Transaction_Type.TOP_UP)
                        .filter(t -> "PayPal".equals(t.getPaymentMethod()))
                        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                        .limit(1)
                        .toList();

                if (!pendingTransactions.isEmpty()) {
                    WalletTransaction transaction = pendingTransactions.get(0);
                    // Only update if transaction is still pending (avoid duplicate updates)
                    if (transaction.getTransactionStatus() == Wallet_Transaction_Status.PENDING) {
                        transaction.setTransactionStatus(Wallet_Transaction_Status.COMPLETED);
                        transaction.setUpdatedAt(LocalDateTime.now());
                        walletTransactionRepository.save(transaction);

                        // Update wallet balance using transaction amount (already in VND)
                        Wallet wallet = transaction.getWallet();
                        BigDecimal currentBalance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
                        BigDecimal transactionAmountVND = BigDecimal.valueOf(transaction.getAmount());
                        BigDecimal newBalance = currentBalance.add(transactionAmountVND);
                        wallet.setBalance(newBalance);
                        walletRepository.save(wallet);
                    } else {
                        System.out.println("Transaction " + transaction.getId() + " is already completed. Skipping wallet update.");
                    }
                }

                // Return success HTML
                String successHtml = "<!DOCTYPE html><html><head><title>Payment Success</title>" +
                        "<meta http-equiv='refresh' content='3;url=http://localhost:3000/member'></head>" +
                        "<body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                        "<h1 style='color: green;'>✓ Payment Successful!</h1>" +
                        "<p>Amount: $" + amountUSD.toString() + " USD</p>" +
                        "<p>You will be redirected to the member portal...</p>" +
                        "</body></html>";

                return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8")
                        .body(successHtml);
            } else {
                throw new RuntimeException("Payment not approved. State: " + payment.getState());
            }
        } catch (Exception e) {
            String errorHtml = "<!DOCTYPE html><html><head><title>Payment Error</title>" +
                    "<meta http-equiv='refresh' content='3;url=http://localhost:3000/'></head>" +
                    "<body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                    "<h1 style='color: red;'>✗ Payment Error</h1>" +
                    "<p>" + e.getMessage() + "</p>" +
                    "<p>You will be redirected to the home page...</p>" +
                    "</body></html>";

            return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8")
                    .body(errorHtml);
        }
    }

    @GetMapping("/paypal/cancel")
    public ResponseEntity<String> paypalCancel() {
        String cancelHtml = "<!DOCTYPE html><html><head><title>Payment Cancelled</title>" +
                "<meta http-equiv='refresh' content='3;url=http://localhost:3000/'></head>" +
                "<body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                "<h1 style='color: orange;'>⚠ Payment Cancelled</h1>" +
                "<p>You cancelled the payment.</p>" +
                "<p>You will be redirected to the home page...</p>" +
                "</body></html>";

        return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8")
                .body(cancelHtml);
    }

    /**
     * Mobile PayPal return endpoint - redirects to deep link after payment execution
     */
    @GetMapping("/paypal/return-mobile")
    public ResponseEntity<String> paypalReturnMobile(
            @RequestParam(required = false) String paymentId,
            @RequestParam(required = false) String PayerID,
            @RequestParam(required = false) String payerId,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String cancel) {

        try {
            // Handle cancel case
            if (cancel != null && (cancel.equals("true") || cancel.equals("1"))) {
                String cancelDeepLink = "iotkitrental://topup?cancel=true";
                String cancelHtml = "<!DOCTYPE html><html><head><title>Payment Cancelled</title>" +
                        "<script>window.location.href = '" + cancelDeepLink + "';</script>" +
                        "<meta http-equiv='refresh' content='0;url=" + cancelDeepLink + "'>" +
                        "</head><body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                        "<h1 style='color: orange;'>⚠ Payment Cancelled</h1>" +
                        "<p>Redirecting to app...</p>" +
                        "<p><a href='" + cancelDeepLink + "'>Click here if not redirected</a></p>" +
                        "</body></html>";

                return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8")
                        .body(cancelHtml);
            }

            // Get payerId from either PayerID or payerId parameter
            String finalPayerId = PayerID != null ? PayerID : payerId;

            if (paymentId == null || finalPayerId == null) {
                throw new RuntimeException("Missing paymentId or PayerID");
            }

            // Check payment state first before executing
            Payment payment = null;
            try {
                Payment existingPayment = payPalService.getPaymentDetails(paymentId);
                // If payment is already approved, skip execution
                if (existingPayment != null && "approved".equalsIgnoreCase(existingPayment.getState())) {
                    System.out.println("Payment " + paymentId + " is already approved. Skipping execution.");
                    payment = existingPayment;
                } else {
                    // Execute payment only if not already approved
                    payment = payPalService.executePayment(paymentId, finalPayerId);
                }
            } catch (PayPalRESTException e) {
                // Handle PAYMENT_ALREADY_DONE error
                if (e.getMessage() != null && e.getMessage().contains("PAYMENT_ALREADY_DONE")) {
                    System.out.println("Payment already done. Getting payment details.");
                    payment = payPalService.getPaymentDetails(paymentId);
                } else {
                    throw e;
                }
            }

            if (payment != null && "approved".equalsIgnoreCase(payment.getState())) {
                // Get transaction amount (USD) from PayPal
                BigDecimal amountUSD = new BigDecimal(payment.getTransactions().get(0).getAmount().getTotal());

                // Find and update transaction
                List<WalletTransaction> pendingTransactions = walletTransactionRepository.findAll()
                        .stream()
                        .filter(t -> t.getTransactionStatus() == Wallet_Transaction_Status.PENDING)
                        .filter(t -> t.getTransactionType() == Wallet_Transaction_Type.TOP_UP)
                        .filter(t -> "PayPal".equals(t.getPaymentMethod()))
                        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                        .limit(1)
                        .toList();

                if (!pendingTransactions.isEmpty()) {
                    WalletTransaction transaction = pendingTransactions.get(0);
                    // Only update if transaction is still pending (avoid duplicate updates)
                    if (transaction.getTransactionStatus() == Wallet_Transaction_Status.PENDING) {
                        transaction.setTransactionStatus(Wallet_Transaction_Status.COMPLETED);
                        transaction.setUpdatedAt(LocalDateTime.now());
                        walletTransactionRepository.save(transaction);

                        // Update wallet balance using transaction amount (already in VND)
                        Wallet wallet = transaction.getWallet();
                        BigDecimal currentBalance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
                        BigDecimal transactionAmountVND = BigDecimal.valueOf(transaction.getAmount());
                        BigDecimal newBalance = currentBalance.add(transactionAmountVND);
                        wallet.setBalance(newBalance);
                        walletRepository.save(wallet);
                    } else {
                        System.out.println("Transaction " + transaction.getId() + " is already completed. Skipping wallet update.");
                    }
                }

                // Build deep link URL
                String deepLink = "iotkitrental://topup?paymentId=" + paymentId + "&PayerID=" + finalPayerId;

                // Return HTML page that redirects to deep link
                String successHtml = "<!DOCTYPE html><html><head><title>Payment Success</title>" +
                        "<script>window.location.href = '" + deepLink + "';</script>" +
                        "<meta http-equiv='refresh' content='0;url=" + deepLink + "'>" +
                        "</head><body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                        "<h1 style='color: green;'>✓ Payment Successful!</h1>" +
                        "<p>Amount: $" + amountUSD.toString() + " USD</p>" +
                        "<p>Redirecting to app...</p>" +
                        "<p><a href='" + deepLink + "'>Click here if not redirected</a></p>" +
                        "</body></html>";

                return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8")
                        .body(successHtml);
            } else {
                throw new RuntimeException("Payment not approved. State: " + (payment != null ? payment.getState() : "null"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Build error deep link
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
            String errorDeepLink = "iotkitrental://topup?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
            String errorHtml = "<!DOCTYPE html><html><head><title>Payment Error</title>" +
                    "<script>window.location.href = '" + errorDeepLink + "';</script>" +
                    "<meta http-equiv='refresh' content='0;url=" + errorDeepLink + "'>" +
                    "</head><body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                    "<h1 style='color: red;'>✗ Payment Error</h1>" +
                    "<p>" + e.getMessage() + "</p>" +
                    "<p>Redirecting to app...</p>" +
                    "<p><a href='" + errorDeepLink + "'>Click here if not redirected</a></p>" +
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
            response.setStatus(IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus.Ok);
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
