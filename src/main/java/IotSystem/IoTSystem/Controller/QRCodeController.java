package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Request.QRCodeDecodeRequest;
import IotSystem.IoTSystem.Model.Request.QRCodeParseRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Service.Implement.QRCodeService;
import com.google.zxing.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/qr-code")
public class QRCodeController {

    /**
     * Decode QR code from Base64 image
     * @param request Contains base64QRCode field with the Base64 encoded QR code image
     * @return Decoded text from QR code
     */
    @PostMapping("/decode")
    public ResponseEntity<ApiResponse<String>> decodeQRCode(@RequestBody QRCodeDecodeRequest request) {
        try {
            if (request.getBase64QRCode() == null || request.getBase64QRCode().trim().isEmpty()) {
                ApiResponse<String> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("Base64 QR code image is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Decode QR code from Base64 string
            String decodedText = QRCodeService.decodeQRCodeFromBase64(request.getBase64QRCode());

            ApiResponse<String> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("QR code decoded successfully");
            response.setData(decodedText);

            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.NotFound);
            errorResponse.setMessage("QR code not found in the image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IOException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Error decoding QR code: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Decode QR code from Base64 string (simple GET endpoint)
     * @param base64QRCode Base64 encoded QR code image
     * @return Decoded text from QR code
     */
    @GetMapping("/decode")
    public ResponseEntity<ApiResponse<String>> decodeQRCodeGet(@RequestParam String base64QRCode) {
        try {
            if (base64QRCode == null || base64QRCode.trim().isEmpty()) {
                ApiResponse<String> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("Base64 QR code image is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Decode QR code from Base64 string
            String decodedText = QRCodeService.decodeQRCodeFromBase64(base64QRCode);

            ApiResponse<String> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("QR code decoded successfully");
            response.setData(decodedText);

            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.NotFound);
            errorResponse.setMessage("QR code not found in the image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IOException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Error decoding QR code: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Parse QR code text or Base64 image (auto-detects format)
     * @param request Contains qrText field with either plain text or Base64-encoded QR image
     * @return Parsed structured information from QR code text
     */
    @PostMapping("/parse")
    public ResponseEntity<ApiResponse<Map<String, Object>>> parseQRCodeText(@RequestBody QRCodeParseRequest request) {
        try {
            if (request.getQrText() == null || request.getQrText().trim().isEmpty()) {
                ApiResponse<Map<String, Object>> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("QR code text or Base64 image is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Parse QR code text (automatically detects if it's Base64 image or plain text)
            Map<String, Object> parsedData = QRCodeService.parseQRCodeText(request.getQrText());

            if (parsedData == null) {
                ApiResponse<Map<String, Object>> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("Invalid QR code format. Please check the QR code text.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            ApiResponse<Map<String, Object>> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("QR code text parsed successfully");
            response.setData(parsedData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Map<String, Object>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Error parsing QR code text: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Parse QR code text or Base64 image from query parameter (GET method)
     * @param qrText Plain text or Base64-encoded QR image from QR code
     * @return Parsed structured information from QR code text
     */
    @GetMapping("/parse")
    public ResponseEntity<ApiResponse<Map<String, Object>>> parseQRCodeTextGet(@RequestParam String qrText) {
        try {
            if (qrText == null || qrText.trim().isEmpty()) {
                ApiResponse<Map<String, Object>> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("QR code text or Base64 image is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Decode URL encoding if needed
            String decodedText = java.net.URLDecoder.decode(qrText, "UTF-8");

            // Parse QR code text (automatically detects if it's Base64 image or plain text)
            Map<String, Object> parsedData = QRCodeService.parseQRCodeText(decodedText);

            if (parsedData == null) {
                ApiResponse<Map<String, Object>> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("Invalid QR code format. Please check the QR code text.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            ApiResponse<Map<String, Object>> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("QR code text parsed successfully");
            response.setData(parsedData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Map<String, Object>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Error parsing QR code text: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
