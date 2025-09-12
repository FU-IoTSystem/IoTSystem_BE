package IotSystem.IoTSystem.Service;


import IotSystem.IoTSystem.Entities.Account;
import IotSystem.IoTSystem.Entities.BorrowingRequest;
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

    public List<BorrowingRequest> getAll() {
        return borrowingRequestRepository.findAll();
    }

    public BorrowingRequest getById(UUID id) {
        return borrowingRequestRepository.findById(id).orElse(null);
    }

    public BorrowingRequest create(BorrowingRequest request) {
        return borrowingRequestRepository.save(request);
    }

    public BorrowingRequest update(UUID id, BorrowingRequest updated) {
        BorrowingRequest existing = borrowingRequestRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setBorrowDate(updated.getBorrowDate());
            existing.setExpectedReturnDate(updated.getExpectedReturnDate());
            existing.setActualReturnDate(updated.getActualReturnDate());
            existing.setStatus(updated.getStatus());
            existing.setNote(updated.getNote());
            existing.setPenaltyAmount(updated.getPenaltyAmount());
            existing.setDepositAmount(updated.getDepositAmount());
            existing.setKit(updated.getKit());
            existing.setRequestedBy(updated.getRequestedBy());
            existing.setGroup(updated.getGroup());
            existing.setApprovedBy(updated.getApprovedBy());
            return borrowingRequestRepository.save(existing);
        }
        return null;
    }

    public void delete(UUID id) {
        borrowingRequestRepository.deleteById(id);
    }
}
