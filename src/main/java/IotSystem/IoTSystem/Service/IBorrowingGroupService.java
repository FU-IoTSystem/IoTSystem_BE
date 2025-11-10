package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.BorrowingGroup;
import IotSystem.IoTSystem.Model.Request.BorrowingGroupRequest;
import IotSystem.IoTSystem.Model.Response.BorrowingGroupResponse;

import java.util.List;
import java.util.UUID;

public interface IBorrowingGroupService {

    // Basic CRUD operations
    List<BorrowingGroupResponse> getAll();

    BorrowingGroupResponse getById(UUID id);

    BorrowingGroupResponse create(BorrowingGroupRequest request);

    BorrowingGroupResponse update(UUID id, BorrowingGroupRequest request);

    void delete(UUID id);

    // Custom operations
    List<BorrowingGroupResponse> getByStudentGroupId(UUID studentGroupId);

    List<BorrowingGroupResponse> getByAccountId(UUID accountId);

    BorrowingGroupResponse getByStudentGroupAndAccount(UUID studentGroupId, UUID accountId);

    List<BorrowingGroupResponse> getByRole(IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles roles);

    List<BorrowingGroupResponse> getByStudentGroupAndRole(UUID studentGroupId, IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles roles);

    boolean existsByStudentGroupAndAccount(UUID studentGroupId, UUID accountId);

    long countByStudentGroupId(UUID studentGroupId);

    List<BorrowingGroupResponse> getByStudentGroupIdWithDetails(UUID studentGroupId);

    // Group management operations
    BorrowingGroupResponse addMemberToGroup(UUID studentGroupId, UUID accountId, IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles roles);

    void removeMemberFromGroup(UUID studentGroupId, UUID accountId);

    BorrowingGroupResponse promoteToLeader(UUID studentGroupId, UUID accountId);

    BorrowingGroupResponse demoteToMember(UUID studentGroupId, UUID accountId);
}
