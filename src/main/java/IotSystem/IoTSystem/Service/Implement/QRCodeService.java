package IotSystem.IoTSystem.Service.Implement;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class QRCodeService {
    
    private static final int QR_CODE_WIDTH = 350;
    private static final int QR_CODE_HEIGHT = 350;
    
    /**
     * Generate QR code as Base64 string
     */
    public static String generateQRCodeBase64(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        byte[] qrCodeBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(qrCodeBytes);
    }
    
    /**
     * Generate QR code as byte array
     */
    public static byte[] generateQRCodeBytes(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        return outputStream.toByteArray();
    }
    
    /**
     * Decode QR code from Base64 string
     * @param base64QRCode Base64 encoded QR code image
     * @return Decoded text content from QR code
     * @throws IOException If image reading fails
     * @throws NotFoundException If QR code is not found in the image
     */
    public static String decodeQRCodeFromBase64(String base64QRCode) throws IOException, NotFoundException {
        // Decode Base64 to byte array
        byte[] imageBytes = Base64.getDecoder().decode(base64QRCode);
        
        // Convert byte array to BufferedImage
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        
        if (bufferedImage == null) {
            throw new IOException("Could not decode image from Base64 string");
        }
        
        // Decode QR code from image
        return decodeQRCodeFromImage(bufferedImage);
    }
    
    /**
     * Decode QR code from byte array
     * @param imageBytes Image bytes containing QR code
     * @return Decoded text content from QR code
     * @throws IOException If image reading fails
     * @throws NotFoundException If QR code is not found in the image
     */
    public static String decodeQRCodeFromBytes(byte[] imageBytes) throws IOException, NotFoundException {
        // Convert byte array to BufferedImage
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        
        if (bufferedImage == null) {
            throw new IOException("Could not decode image from byte array");
        }
        
        // Decode QR code from image
        return decodeQRCodeFromImage(bufferedImage);
    }
    
    /**
     * Decode QR code from BufferedImage
     * @param bufferedImage Image containing QR code
     * @return Decoded text content from QR code
     * @throws NotFoundException If QR code is not found in the image
     */
    public static String decodeQRCodeFromImage(BufferedImage bufferedImage) throws NotFoundException {
        // Convert image to binary bitmap
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        // Read QR code
        MultiFormatReader reader = new MultiFormatReader();
        Result result = reader.decode(bitmap);
        
        return result.getText();
    }
    
    /**
     * Parse QR code text and extract structured information
     * Automatically detects if input is Base64 image or plain text
     * @param qrInput Can be either Base64-encoded QR image or plain text from QR code
     * @return Map containing parsed information with type and data fields
     */
    public static Map<String, Object> parseQRCodeText(String qrInput) {
        if (qrInput == null || qrInput.trim().isEmpty()) {
            return null;
        }
        
        String qrText = qrInput;
        
        // Try to detect if input is Base64-encoded image or plain text
        // Plain text QR codes contain recognizable markers like "===" and "INFO"
        // Base64 images are typically long strings without newlines or special markers
        try {
            boolean looksLikePlainText = qrInput.contains("=== BORROWING REQUEST INFO ===") || 
                                       qrInput.contains("=== COMPONENT RENTAL REQUEST ===") ||
                                       (qrInput.contains("\n") && (qrInput.contains("Borrower:") || qrInput.contains("Kit Name:")));
            
            // If it doesn't look like plain text, try decoding as Base64 image
            if (!looksLikePlainText) {
                String base64Data = qrInput;
                
                // Remove data URL prefix if present (e.g., "data:image/png;base64,")
                if (qrInput.contains("data:image") && qrInput.contains(",")) {
                    base64Data = qrInput.substring(qrInput.indexOf(",") + 1);
                }
                
                // Check if it might be Base64 (long string, contains Base64 characters)
                if (base64Data.length() > 500 && base64Data.matches("^[A-Za-z0-9+/=\\s]+$")) {
                    try {
                        // Try to decode as Base64 image
                        qrText = decodeQRCodeFromBase64(base64Data.trim());
                    } catch (NotFoundException e) {
                        // QR code not found in image - might not be a valid QR image
                        // Treat as plain text
                        qrText = qrInput;
                    } catch (Exception e) {
                        // Decoding failed - treat as plain text
                        qrText = qrInput;
                    }
                }
            }
        } catch (Exception e) {
            // Fallback: treat as plain text
            qrText = qrInput;
        }
        
        Map<String, Object> result = new HashMap<>();
        Map<String, String> data = new HashMap<>();
        
        String[] lines = qrText.split("\n");
        
        // Check if it's a borrowing request or component rental request
        if (qrText.contains("=== BORROWING REQUEST INFO ===")) {
            result.put("type", "BORROWING_REQUEST");
            
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("Borrower: ")) {
                    data.put("borrower", line.replace("Borrower: ", "").trim());
                } else if (line.startsWith("Borrower ID: ")) {
                    data.put("borrowerId", line.replace("Borrower ID: ", "").trim());
                } else if (line.startsWith("Kit Name: ")) {
                    data.put("kitName", line.replace("Kit Name: ", "").trim());
                } else if (line.startsWith("Kit ID: ")) {
                    data.put("kitId", line.replace("Kit ID: ", "").trim());
                } else if (line.startsWith("Request Type: ")) {
                    data.put("requestType", line.replace("Request Type: ", "").trim());
                } else if (line.startsWith("Reason: ")) {
                    data.put("reason", line.replace("Reason: ", "").trim());
                } else if (line.startsWith("Expected Return Date: ")) {
                    data.put("expectReturnDate", line.replace("Expected Return Date: ", "").trim());
                } else if (line.startsWith("Status: ")) {
                    data.put("status", line.replace("Status: ", "").trim());
                }
            }
        } else if (qrText.contains("=== COMPONENT RENTAL REQUEST ===")) {
            result.put("type", "COMPONENT_RENTAL");
            
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("Borrower: ")) {
                    data.put("borrower", line.replace("Borrower: ", "").trim());
                } else if (line.startsWith("Borrower ID: ")) {
                    data.put("borrowerId", line.replace("Borrower ID: ", "").trim());
                } else if (line.startsWith("Component Name: ")) {
                    data.put("componentName", line.replace("Component Name: ", "").trim());
                } else if (line.startsWith("Component ID: ")) {
                    data.put("componentId", line.replace("Component ID: ", "").trim());
                } else if (line.startsWith("Quantity: ")) {
                    data.put("quantity", line.replace("Quantity: ", "").trim());
                } else if (line.startsWith("Price per Unit: ")) {
                    String priceStr = line.replace("Price per Unit: ", "").replace(" VND", "").trim();
                    data.put("pricePerUnit", priceStr);
                } else if (line.startsWith("Total Amount: ")) {
                    String amountStr = line.replace("Total Amount: ", "").replace(" VND", "").trim();
                    data.put("totalAmount", amountStr);
                } else if (line.startsWith("Reason: ")) {
                    data.put("reason", line.replace("Reason: ", "").trim());
                } else if (line.startsWith("Expected Return Date: ")) {
                    data.put("expectReturnDate", line.replace("Expected Return Date: ", "").trim());
                } else if (line.startsWith("Status: ")) {
                    data.put("status", line.replace("Status: ", "").trim());
                }
            }
        } else {
            // Unknown format
            return null;
        }
        
        result.put("data", data);
        return data.isEmpty() ? null : result;
    }
}

