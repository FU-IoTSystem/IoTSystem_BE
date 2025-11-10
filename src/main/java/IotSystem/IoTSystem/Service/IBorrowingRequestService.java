package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Model.Request.BorrowingRequestCreateRequest;
import IotSystem.IoTSystem.Model.Request.ComponentRentalRequest;
import IotSystem.IoTSystem.Model.Response.BorrowingRequestResponse;

import java.util.List;
import java.util.UUID;

public interface IBorrowingRequestService {
    List<BorrowingRequestResponse> getAll();

    BorrowingRequestResponse create(BorrowingRequestCreateRequest request);

    BorrowingRequestResponse createComponentRequest(ComponentRentalRequest request);

    BorrowingRequestResponse update(UUID id, BorrowingRequest request);

    void delete(UUID id);

    List<BorrowingRequestResponse> getByStatus();

    BorrowingRequestResponse getById(UUID id);

    List<BorrowingRequestResponse> getByUser(UUID userID);

    List<BorrowingRequestResponse> getByStatuses(List<String> statuses);
}
