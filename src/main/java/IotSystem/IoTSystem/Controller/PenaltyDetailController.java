package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.PenaltyDetail;
import IotSystem.IoTSystem.Model.Request.PenaltyDetailRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Response.PenaltyDetailResponse;
import IotSystem.IoTSystem.Service.IPenaltyDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/penalty-details")
public class PenaltyDetailController {

    @Autowired
    private IPenaltyDetailService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PenaltyDetail>>> getAll() {
        List<PenaltyDetail> details = service.getAll();
        ApiResponse<List<PenaltyDetail>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched penalty details successfully");
        response.setData(details);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PenaltyDetail>> getById(@PathVariable UUID id) {
        PenaltyDetail detail = service.getById(id);
        ApiResponse<PenaltyDetail> response = new ApiResponse<>();
        if (detail != null) {
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Fetched penalty detail successfully");
            response.setData(detail);
            return ResponseEntity.ok(response);
        } else {
            response.setStatus(HTTPStatus.NotFound);
            response.setMessage("Penalty detail not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PenaltyDetail>> create(@RequestBody PenaltyDetailRequest request) {
        PenaltyDetail detail = service.create(request);
        ApiResponse<PenaltyDetail> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Created penalty detail successfully");
        response.setData(detail);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-multiple")
    public ResponseEntity<ApiResponse<List<PenaltyDetail>>> createMultiple(@RequestBody List<PenaltyDetailRequest> requests) {
        List<PenaltyDetail> details = service.createMultiple(requests);
        ApiResponse<List<PenaltyDetail>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Created penalty details successfully");
        response.setData(details);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PenaltyDetail>> update(@PathVariable UUID id, @RequestBody PenaltyDetailRequest request) {
        PenaltyDetail detail = service.update(id, request);
        ApiResponse<PenaltyDetail> response = new ApiResponse<>();
        if (detail != null) {
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Updated penalty detail successfully");
            response.setData(detail);
            return ResponseEntity.ok(response);
        } else {
            response.setStatus(HTTPStatus.NotFound);
            response.setMessage("Penalty detail not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Deleted penalty detail successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/penalty/{penaltyId}")
    public ResponseEntity<ApiResponse<List<PenaltyDetailResponse>>> findByPenaltyId(@PathVariable UUID penaltyId) {
        List<PenaltyDetailResponse> details = service.findByPenaltyId(penaltyId);
        ApiResponse<List<PenaltyDetailResponse>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched penalty details by penalty ID successfully");
        response.setData(details);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/policies/{policiesId}")
    public ResponseEntity<ApiResponse<List<PenaltyDetail>>> findByPoliciesId(@PathVariable UUID policiesId) {
        List<PenaltyDetail> details = service.findByPoliciesId(policiesId);
        ApiResponse<List<PenaltyDetail>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched penalty details by policies ID successfully");
        response.setData(details);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("File is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Validate file type (only images)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("File must be an image");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Validate file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>();
                errorResponse.setStatus(HTTPStatus.BadRequest);
                errorResponse.setMessage("File size must not exceed 5MB");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Convert image to base64
            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String imageDataUrl = "data:" + contentType + ";base64," + base64Image;

            // Return image URL (base64 data URL)
            Map<String, String> result = new HashMap<>();
            result.put("imageUrl", imageDataUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("contentType", contentType);
            result.put("size", String.valueOf(file.getSize()));

            ApiResponse<Map<String, String>> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Image uploaded successfully");
            response.setData(result);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to process image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

