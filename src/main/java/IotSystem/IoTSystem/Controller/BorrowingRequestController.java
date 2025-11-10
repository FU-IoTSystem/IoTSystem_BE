package IotSystem.IoTSystem.Controller;


import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Request.BorrowingRequestCreateRequest;
import IotSystem.IoTSystem.Model.Request.ComponentRentalRequest;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import IotSystem.IoTSystem.Model.Response.BorrowingRequestResponse;
import IotSystem.IoTSystem.Service.IBorrowingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/borrowing-requests")
public class BorrowingRequestController {

    @Autowired
    private IBorrowingRequestService borrowingRequestService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BorrowingRequestResponse>>> getAll() {
        List<BorrowingRequestResponse> requests = borrowingRequestService.getAll();

        ApiResponse<List<BorrowingRequestResponse>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched borrowing requests successfully");
        response.setData(requests);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/getAPPROVED")
    public ResponseEntity<ApiResponse<List<BorrowingRequestResponse>>> getAllByStatus(){
        List<BorrowingRequestResponse> borrow = borrowingRequestService.getByStatus();

        ApiResponse<List<BorrowingRequestResponse>> response = new ApiResponse<>();
        response.setData(borrow);
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched borrowing requests by status successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-statuses")
    public ResponseEntity<ApiResponse<List<BorrowingRequestResponse>>> getByStatuses(@RequestParam List<String> statuses) {
        List<BorrowingRequestResponse> borrows = borrowingRequestService.getByStatuses(statuses);

        ApiResponse<List<BorrowingRequestResponse>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched borrowing requests by statuses successfully");
        response.setData(borrows);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get_by/{id}")
    public ResponseEntity<ApiResponse<BorrowingRequestResponse>> getById(@PathVariable UUID id) {
        BorrowingRequestResponse borrowResponse = borrowingRequestService.getById(id);

        ApiResponse<BorrowingRequestResponse> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched borrowing request successfully");
        response.setData(borrowResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/post")
    @PreAuthorize("hasAnyRole('STUDENT','LECTURER')")
    public ResponseEntity<ApiResponse<BorrowingRequestResponse>> create(@RequestBody BorrowingRequestCreateRequest request) {
        BorrowingRequestResponse borrowResponse = borrowingRequestService.create(request);

        ApiResponse<BorrowingRequestResponse> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Borrowing request created successfully");
        response.setData(borrowResponse);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BorrowingRequestResponse>> update(@PathVariable UUID id, @RequestBody BorrowingRequest request) {
        BorrowingRequestResponse updatedResponse = borrowingRequestService.update(id, request);

        ApiResponse<BorrowingRequestResponse> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Borrowing request updated successfully");
        response.setData(updatedResponse);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable UUID id) {
        borrowingRequestService.delete(id);

        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Borrowing request deleted successfully");
        response.setData("Deleted request ID: " + id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BorrowingRequestResponse>>> getByUser(@PathVariable UUID userId) {
        List<BorrowingRequestResponse> requests = borrowingRequestService.getByUser(userId);

        ApiResponse<List<BorrowingRequestResponse>> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.Ok);
        response.setMessage("Fetched user borrowing requests successfully");
        response.setData(requests);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/component")
    @PreAuthorize("hasAnyRole('STUDENT','LECTURER')")
    public ResponseEntity<ApiResponse<BorrowingRequestResponse>> createComponentRequest(@RequestBody IotSystem.IoTSystem.Model.Request.ComponentRentalRequest request) {
        try {
            BorrowingRequestResponse borrowResponse = borrowingRequestService.createComponentRequest(request);

            ApiResponse<BorrowingRequestResponse> response = new ApiResponse<>();
            response.setStatus(HTTPStatus.Ok);
            response.setMessage("Component rental request created successfully. Waiting for admin approval.");
            response.setData(borrowResponse);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<BorrowingRequestResponse> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(HTTPStatus.InternalServerError);
            errorResponse.setMessage("Failed to create component rental request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
