package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Config.PayPalConfig;
import IotSystem.IoTSystem.Model.Request.PayPalPaymentRequest;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayPalService {

    @Autowired
    private PayPalConfig payPalConfig;

    private APIContext getAPIContext() {
        String clientId = payPalConfig.getClientId();
        String clientSecret = payPalConfig.getClientSecret();
        String mode = payPalConfig.getMode();
        
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalStateException("PayPal Client ID is not configured");
        }
        if (clientSecret == null || clientSecret.isEmpty()) {
            throw new IllegalStateException("PayPal Client Secret is not configured");
        }
        if (mode == null || mode.isEmpty()) {
            mode = "sandbox"; // default to sandbox
        }
        
        System.out.println("Creating PayPal APIContext with mode: " + mode);
        
        return new APIContext(clientId, clientSecret, mode);
    }

    /**
     * Create PayPal payment order
     * @param request Payment request with amount and description
     * @return Payment object with approval URL
     */
    public Payment createPayment(PayPalPaymentRequest request) throws PayPalRESTException {
        return createPayment(request, null, null);
    }

    /**
     * Create PayPal payment order with custom return/cancel URLs
     * @param request Payment request with amount and description
     * @param returnUrl Custom return URL (null to use default)
     * @param cancelUrl Custom cancel URL (null to use default)
     * @return Payment object with approval URL
     */
    public Payment createPayment(PayPalPaymentRequest request, String returnUrl, String cancelUrl) throws PayPalRESTException {
        APIContext apiContext = getAPIContext();

        // Set payer information
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        // Set redirect URLs (use custom URLs if provided, otherwise use config defaults)
        RedirectUrls redirectUrls = new RedirectUrls();
        String finalReturnUrl = (returnUrl != null && !returnUrl.isEmpty()) 
            ? returnUrl + (returnUrl.contains("?") ? "&" : "?") + "orderId=" + request.getOrderId()
            : payPalConfig.getReturnUrl() + "?orderId=" + request.getOrderId();
        String finalCancelUrl = (cancelUrl != null && !cancelUrl.isEmpty())
            ? cancelUrl + (cancelUrl.contains("?") ? "&" : "?") + "cancel=true"
            : payPalConfig.getCancelUrl() + "?cancel=true";
        
        redirectUrls.setCancelUrl(finalCancelUrl);
        redirectUrls.setReturnUrl(finalReturnUrl);

        // Set amount details
        Amount amount = new Amount();
        String currency = (request.getCurrency() != null && !request.getCurrency().isEmpty()) 
            ? request.getCurrency() 
            : payPalConfig.getCurrency();
        amount.setCurrency(currency);
        
        // Convert amount to string with 2 decimal places
        BigDecimal amountValue = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        amount.setTotal(amountValue.toString());

        // Set transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(request.getDescription());
        transaction.setInvoiceNumber(request.getOrderId());

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        // Set payment
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setRedirectUrls(redirectUrls);
        payment.setTransactions(transactions);

        // Create payment
        Payment createdPayment = payment.create(apiContext);

        // Log payment creation
        System.out.println("=== PayPal Payment Created ===");
        System.out.println("Payment ID: " + createdPayment.getId());
        System.out.println("Order ID: " + request.getOrderId());
        System.out.println("Amount: " + amountValue + " " + request.getCurrency());
        System.out.println("Approval URL: " + getApprovalUrl(createdPayment));
        System.out.println("=============================");

        return createdPayment;
    }

    /**
     * Execute PayPal payment after user approval
     * @param paymentId PayPal payment ID
     * @param payerId Payer ID from PayPal
     * @return Executed payment object
     */
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        APIContext apiContext = getAPIContext();

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        Payment executedPayment = payment.execute(apiContext, paymentExecution);

        // Log payment execution
        System.out.println("=== PayPal Payment Executed ===");
        System.out.println("Payment ID: " + executedPayment.getId());
        System.out.println("State: " + executedPayment.getState());
        System.out.println("===============================");

        return executedPayment;
    }

    /**
     * Get payment details by payment ID
     * @param paymentId PayPal payment ID
     * @return Payment object
     */
    public Payment getPaymentDetails(String paymentId) throws PayPalRESTException {
        APIContext apiContext = getAPIContext();
        return Payment.get(apiContext, paymentId);
    }

    /**
     * Extract approval URL from payment object
     * @param payment PayPal payment object
     * @return Approval URL string
     */
    public String getApprovalUrl(Payment payment) {
        List<Links> links = payment.getLinks();
        for (Links link : links) {
            if (link.getRel().equals("approval_url")) {
                return link.getHref();
            }
        }
        return null;
    }
}

