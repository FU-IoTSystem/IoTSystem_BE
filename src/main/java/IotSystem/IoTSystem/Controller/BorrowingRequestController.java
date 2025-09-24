package IotSystem.IoTSystem.Controller;


import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Service.IBorrowingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/borrowing-requests")
public class BorrowingRequestController {

    @Autowired
    private IBorrowingRequestService borrowingRequestService;

    @GetMapping
    public List<BorrowingRequest> getAll() {
        return borrowingRequestService.getAll();
    }

    @GetMapping("/get_by/{id}")
    public BorrowingRequest getById(@PathVariable UUID id) {
        return borrowingRequestService.getById(id);
    }

    @PostMapping("/post")
    public BorrowingRequest create(@RequestBody BorrowingRequest request) {
        return borrowingRequestService.create(request);
    }

    @PutMapping("/update/{id}")
    public BorrowingRequest update(@PathVariable UUID id, @RequestBody BorrowingRequest request) {
        return borrowingRequestService.update(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        borrowingRequestService.delete(id);
    }
}
