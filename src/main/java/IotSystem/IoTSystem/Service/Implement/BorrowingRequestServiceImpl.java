package IotSystem.IoTSystem.Service.Implement;


import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Service.IBorrowingRequestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BorrowingRequestServiceImpl implements IBorrowingRequestService {


    @Override
    public List<BorrowingRequest> getAll() {
        return List.of();
    }

    @Override
    public BorrowingRequest create(BorrowingRequest request) {
        return null;
    }

    @Override
    public BorrowingRequest update(UUID id, BorrowingRequest request) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public BorrowingRequest getById(UUID id) {
        return null;
    }
}
