package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Request.RequestKitComponentRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Response.RequestKitComponentResponse;
import IotSystem.IoTSystem.Service.IRequestKitComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/request-kit-components")
public class RequestKitComponentController {
    
    @Autowired
    private IRequestKitComponentService service;
    
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<RequestKitComponentResponse>>> getAll() {
        try {
            List<RequestKitComponentResponse> components = service.getAll().stream()
                    .map(item -> service.getById(item.getId()))
                    .collect(java.util.stream.Collectors.toList());
            
            ApiResponse<List<RequestKitComponentResponse>> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Fetched request kit components successfully");
            response.setData(components);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<RequestKitComponentResponse>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to fetch request kit components: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RequestKitComponentResponse>> getById(@PathVariable UUID id) {
        try {
            RequestKitComponentResponse component = service.getById(id);
            
            ApiResponse<RequestKitComponentResponse> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Fetched request kit component successfully");
            response.setData(component);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<RequestKitComponentResponse> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to fetch request kit component: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/by-request/{requestId}")
    public ResponseEntity<ApiResponse<List<RequestKitComponentResponse>>> getByRequestId(@PathVariable UUID requestId) {
        try {
            List<RequestKitComponentResponse> components = service.getByRequestId(requestId);
            
            ApiResponse<List<RequestKitComponentResponse>> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Fetched components for request successfully");
            response.setData(components);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<RequestKitComponentResponse>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to fetch components for request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RequestKitComponentResponse>> create(@RequestBody RequestKitComponentRequest request) {
        try {
            RequestKitComponentResponse component = service.create(request);
            
            ApiResponse<RequestKitComponentResponse> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Created request kit component successfully");
            response.setData(component);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<RequestKitComponentResponse> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to create request kit component: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping("/create-multiple")
    public ResponseEntity<ApiResponse<List<RequestKitComponentResponse>>> createMultiple(@RequestBody List<RequestKitComponentRequest> requests) {
        try {
            List<RequestKitComponentResponse> components = service.createMultiple(requests);
            
            ApiResponse<List<RequestKitComponentResponse>> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Created request kit components successfully");
            response.setData(components);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<RequestKitComponentResponse>> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to create request kit components: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<RequestKitComponentResponse>> update(@PathVariable UUID id, @RequestBody RequestKitComponentRequest request) {
        try {
            RequestKitComponentResponse component = service.update(id, request);
            
            ApiResponse<RequestKitComponentResponse> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Updated request kit component successfully");
            response.setData(component);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<RequestKitComponentResponse> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to update request kit component: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        try {
            service.delete(id);
            
            ApiResponse<Void> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Deleted request kit component successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Void> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to delete request kit component: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @DeleteMapping("/delete-by-request/{requestId}")
    public ResponseEntity<ApiResponse<Void>> deleteByRequestId(@PathVariable UUID requestId) {
        try {
            service.deleteByRequestId(requestId);
            
            ApiResponse<Void> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Deleted request kit components successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Void> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to delete request kit components: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

