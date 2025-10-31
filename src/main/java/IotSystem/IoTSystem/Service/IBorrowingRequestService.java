package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;

import java.util.List;
import java.util.UUID;

public interface IBorrowingRequestService {
    List<BorrowingRequest> getAll();

    BorrowingRequest create(BorrowingRequest request);

    BorrowingRequest update(UUID id, BorrowingRequest request);

    void delete(UUID id);

    BorrowingRequest getById(UUID id);
}
