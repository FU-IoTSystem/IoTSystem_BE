package IotSystem.IoTSystem.Service.Implement;

//import com.google.gson.Gson;

import IotSystem.IoTSystem.Config.VNPayConfig;
import IotSystem.IoTSystem.Model.Request.VNPayPaymentRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {

    @Autowired
    private VNPayConfig vnPayConfig;

    public String createPaymentUrl(VNPayPaymentRequest request, HttpServletRequest httpServletRequest) throws Exception {
        String vnpVersion = vnPayConfig.getVersion();
        String vnpCommand = vnPayConfig.getCommand();
        String vnpTmnCode = vnPayConfig.getTmnCode();
        String vnpAmount = String.valueOf(request.getAmount() * 100); // Convert to cents
        String vnpCurrencyCode = vnPayConfig.getCurrencyCode();
        String vnpTxnRef = request.getOrderId();
        String vnpOrderInfo = request.getOrderInfo();
        String vnpOrderType = vnPayConfig.getOrderType();
        String vnpLocale = vnPayConfig.getLocale();
        String vnpReturnUrl = vnPayConfig.getVnpReturnUrl();
        String vnpIpAddr = getIpAddress(httpServletRequest);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = dateFormat.format(new Date());
        
        // Calculate expire date (15 minutes from now)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, vnPayConfig.getDefaultTimeout() / 60);
        String vnpExpireDate = dateFormat.format(cal.getTime());

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnpVersion);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", vnpAmount);
        vnpParams.put("vnp_CurrCode", vnpCurrencyCode);
        vnpParams.put("vnp_TxnRef", vnpTxnRef);
        vnpParams.put("vnp_OrderInfo", vnpOrderInfo);
        vnpParams.put("vnp_OrderType", vnpOrderType);
        vnpParams.put("vnp_Locale", vnpLocale);
        vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
        vnpParams.put("vnp_IpAddr", vnpIpAddr);
        vnpParams.put("vnp_CreateDate", vnpCreateDate);
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        // Add bank code if exists
        if (request.getBankCode() != null && !request.getBankCode().isEmpty()) {
            vnpParams.put("vnp_BankCode", request.getBankCode());
        }

        // Filter out null/empty values
        Map<String, String> filteredParams = new HashMap<>();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                filteredParams.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Sort params by key
        List<String> fieldNames = new ArrayList<>(filteredParams.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = filteredParams.get(fieldName);
            
            // Build hash data (without URL encoding)
            hashData.append(fieldName).append("=").append(fieldValue);
            
            // Build query string with URL encoding  
            query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8))
                 .append("=")
                 .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
            
            // Append & if not last element
            if (i < fieldNames.size() - 1) {
                hashData.append("&");
                query.append("&");
            }
        }
        
        String hashString = hashData.toString();
        String vnpSecureHash = hmacSHA512(vnPayConfig.getSecretKey(), hashString);
        
        // Append secure hash to query
        query.append("&vnp_SecureHash=").append(vnpSecureHash);
        String queryUrl = query.toString();
        
        // Debug logging
        System.out.println("\n========== VNPay Hash Debug ==========");
        System.out.println("Hash String (for signature): " + hashString);
        System.out.println("Secret Key length: " + vnPayConfig.getSecretKey().length());
        System.out.println("Secret Key: " + vnPayConfig.getSecretKey());
        System.out.println("Calculated Hash: " + vnpSecureHash);
        System.out.println("Hash length: " + vnpSecureHash.length());
        System.out.println("\nFull Payment URL:");
        System.out.println(vnPayConfig.getVnpPayUrl() + "?" + queryUrl);
        System.out.println("========================================\n");
        
        String paymentUrl = vnPayConfig.getVnpPayUrl() + "?" + queryUrl;
        
        return paymentUrl;
    }

    public Map<String, String> processCallback(Map<String, String> params) {
        String vnpSecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");
        
        // Filter out null/empty values
        Map<String, String> filteredParams = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                filteredParams.put(entry.getKey(), entry.getValue());
            }
        }
        
        List<String> fieldNames = new ArrayList<>(filteredParams.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = filteredParams.get(fieldName);
            
            hashData.append(fieldName).append("=").append(fieldValue);
            if (i < fieldNames.size() - 1) {
                hashData.append("&");
            }
        }
        
        String secureHash = hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
        
        Map<String, String> result = new HashMap<>();
        result.put("success", vnpSecureHash.equals(secureHash) ? "true" : "false");
        result.put("message", vnpSecureHash.equals(secureHash) ? "Signature valid" : "Signature invalid");
        result.putAll(params);
        
        return result;
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmacSHA512.init(secretKeySpec);
            byte[] digest = hmacSHA512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        // Convert IPv6 localhost to IPv4 for VNPay compatibility
        if ("0:0:0:0:0:0:0:1".equals(ipAddress) || "::1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }
        
        return ipAddress;
    }
}

