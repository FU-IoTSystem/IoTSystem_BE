package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles;
import IotSystem.IoTSystem.Model.Request.BorrowingGroupRequest;
import IotSystem.IoTSystem.Model.Response.BorrowingGroupResponse;
import IotSystem.IoTSystem.Service.IBorrowingGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/borrowing-groups")
public class BorrowingGroupController {

    @Autowired
    private IBorrowingGroupService borrowingGroupService;

    @GetMapping
    public ResponseEntity<List<BorrowingGroupResponse>> getAll() {
        List<BorrowingGroupResponse> responses = borrowingGroupService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowingGroupResponse> getById(@PathVariable UUID id) {
        BorrowingGroupResponse response = borrowingGroupService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BorrowingGroupResponse> create(@RequestBody BorrowingGroupRequest request) {
        BorrowingGroupResponse response = borrowingGroupService.create(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BorrowingGroupResponse> update(@PathVariable UUID id, @RequestBody BorrowingGroupRequest request) {
        BorrowingGroupResponse response = borrowingGroupService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        borrowingGroupService.delete(id);
        return ResponseEntity.ok().build();
    }

    // Custom endpoints
    @GetMapping("/student-group/{studentGroupId}")
    public ResponseEntity<List<BorrowingGroupResponse>> getByStudentGroupId(@PathVariable UUID studentGroupId) {
        List<BorrowingGroupResponse> responses = borrowingGroupService.getByStudentGroupId(studentGroupId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<BorrowingGroupResponse>> getByAccountId(@PathVariable UUID accountId) {
        List<BorrowingGroupResponse> responses = borrowingGroupService.getByAccountId(accountId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/student-group/{studentGroupId}/account/{accountId}")
    public ResponseEntity<BorrowingGroupResponse> getByStudentGroupAndAccount(
            @PathVariable UUID studentGroupId,
            @PathVariable UUID accountId) {
        BorrowingGroupResponse response = borrowingGroupService.getByStudentGroupAndAccount(studentGroupId, accountId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/{roles}")
    public ResponseEntity<List<BorrowingGroupResponse>> getByRole(@PathVariable GroupRoles roles) {
        List<BorrowingGroupResponse> responses = borrowingGroupService.getByRole(roles);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/student-group/{studentGroupId}/role/{roles}")
    public ResponseEntity<List<BorrowingGroupResponse>> getByStudentGroupAndRole(
            @PathVariable UUID studentGroupId,
            @PathVariable GroupRoles roles) {
        List<BorrowingGroupResponse> responses = borrowingGroupService.getByStudentGroupAndRole(studentGroupId, roles);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/student-group/{studentGroupId}/count")
    public ResponseEntity<Long> countByStudentGroupId(@PathVariable UUID studentGroupId) {
        long count = borrowingGroupService.countByStudentGroupId(studentGroupId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/student-group/{studentGroupId}/details")
    public ResponseEntity<List<BorrowingGroupResponse>> getByStudentGroupIdWithDetails(@PathVariable UUID studentGroupId) {
        List<BorrowingGroupResponse> responses = borrowingGroupService.getByStudentGroupIdWithDetails(studentGroupId);
        return ResponseEntity.ok(responses);
    }

    // Group management endpoints
    @PostMapping("/add-member")
    public ResponseEntity<BorrowingGroupResponse> addMemberToGroup(@RequestBody BorrowingGroupRequest request) {
        BorrowingGroupResponse response = borrowingGroupService.addMemberToGroup(
                request.getStudentGroupId(),
                request.getAccountId(),
                request.getRoles()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/student-group/{studentGroupId}/account/{accountId}")
    public ResponseEntity<Void> removeMemberFromGroup(
            @PathVariable UUID studentGroupId,
            @PathVariable UUID accountId) {
        borrowingGroupService.removeMemberFromGroup(studentGroupId, accountId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/promote-leader")
    public ResponseEntity<BorrowingGroupResponse> promoteToLeader(@RequestBody BorrowingGroupRequest request) {
        BorrowingGroupResponse response = borrowingGroupService.promoteToLeader(
                request.getStudentGroupId(),
                request.getAccountId()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/demote-member")
    public ResponseEntity<BorrowingGroupResponse> demoteToMember(@RequestBody BorrowingGroupRequest request) {
        BorrowingGroupResponse response = borrowingGroupService.demoteToMember(
                request.getStudentGroupId(),
                request.getAccountId()
        );
        return ResponseEntity.ok(response);
    }
}
