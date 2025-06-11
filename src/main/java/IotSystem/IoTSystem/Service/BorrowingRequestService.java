package IotSystem.IoTSystem.Service;


import IotSystem.IoTSystem.DTOs.Mappers.BorrowingRequestMapper;
import IotSystem.IoTSystem.DTOs.Response.BorrowingResponseDTO;
import IotSystem.IoTSystem.Entities.Account;
import IotSystem.IoTSystem.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Entities.Enum.BorrowingRequestStatus;
import IotSystem.IoTSystem.Repository.BorrowingRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BorrowingRequestService {

    @Autowired
    private BorrowingRequestRepository borrowingRequestRepository;

    public void approveRequest(UUID id, Account approver) {
        BorrowingRequest request = borrowingRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("not found"));
        request.approve(approver);
        borrowingRequestRepository.save(request);
    }


    public List<BorrowingResponseDTO> getBorrowingsByClassAndStatus(UUID  classId, BorrowingRequestStatus status) {

        List<BorrowingRequest> requests = borrowingRequestRepository.findByGroup_Clazz_IdAndStatus(classId, status);
        return requests.stream()
                .map(BorrowingRequestMapper::toDTO)
                .collect(Collectors.toList());

    }
}
