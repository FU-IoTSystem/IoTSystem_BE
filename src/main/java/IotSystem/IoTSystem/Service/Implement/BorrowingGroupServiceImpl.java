package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.BorrowingGroup;
import IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles;
import IotSystem.IoTSystem.Model.Entities.StudentGroup;
import IotSystem.IoTSystem.Model.Mappers.BorrowingGroupMapper;
import IotSystem.IoTSystem.Model.Request.BorrowingGroupRequest;
import IotSystem.IoTSystem.Model.Response.BorrowingGroupResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.BorrowingGroupRepository;
import IotSystem.IoTSystem.Repository.StudentGroupRepository;
import IotSystem.IoTSystem.Service.IBorrowingGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BorrowingGroupServiceImpl implements IBorrowingGroupService {

    @Autowired
    private BorrowingGroupRepository borrowingGroupRepository;

    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<BorrowingGroupResponse> getAll() {
        List<BorrowingGroup> borrowingGroups = borrowingGroupRepository.findAll();
        return borrowingGroups.stream()
                .map(BorrowingGroupMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BorrowingGroupResponse getById(UUID id) {
        BorrowingGroup borrowingGroup = borrowingGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowingGroup not found with id: " + id));
        return BorrowingGroupMapper.toResponse(borrowingGroup);
    }

    @Override
    public BorrowingGroupResponse create(BorrowingGroupRequest request) {
        // Validate student group exists
        StudentGroup studentGroup = studentGroupRepository.findById(request.getStudentGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("StudentGroup not found with id: " + request.getStudentGroupId()));

        // Validate account exists
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + request.getAccountId()));

        // Check if account is already in the group
        if (borrowingGroupRepository.existsByStudentGroupIdAndAccountId(request.getStudentGroupId(), request.getAccountId())) {
            throw new IllegalArgumentException("Account is already a member of this group");
        }

        // Create borrowing group
        BorrowingGroup borrowingGroup = BorrowingGroupMapper.toEntity(request, studentGroup, account);
        // Set isActive: default to true if not specified in request
        if (request.getIsActive() == null) {
            borrowingGroup.setActive(true);
        }
        BorrowingGroup savedBorrowingGroup = borrowingGroupRepository.save(borrowingGroup);

        return BorrowingGroupMapper.toResponse(savedBorrowingGroup);
    }

    @Override
    public BorrowingGroupResponse update(UUID id, BorrowingGroupRequest request) {
        BorrowingGroup existingBorrowingGroup = borrowingGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowingGroup not found with id: " + id));

        // Update fields
        existingBorrowingGroup.setRoles(request.getRoles());

        // Update isActive if provided
        if (request.getIsActive() != null) {
            existingBorrowingGroup.setActive(request.getIsActive());
        }

        // Update relationships if provided
        if (request.getStudentGroupId() != null) {
            StudentGroup studentGroup = studentGroupRepository.findById(request.getStudentGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("StudentGroup not found with id: " + request.getStudentGroupId()));
            existingBorrowingGroup.setStudentGroup(studentGroup);
        }

        if (request.getAccountId() != null) {
            Account account = accountRepository.findById(request.getAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + request.getAccountId()));
            existingBorrowingGroup.setAccount(account);
        }

        BorrowingGroup updatedBorrowingGroup = borrowingGroupRepository.save(existingBorrowingGroup);
        return BorrowingGroupMapper.toResponse(updatedBorrowingGroup);
    }

    @Override
    public void delete(UUID id) {
        if (!borrowingGroupRepository.existsById(id)) {
            throw new ResourceNotFoundException("BorrowingGroup not found with id: " + id);
        }
        borrowingGroupRepository.deleteById(id);
    }

    @Override
    public List<BorrowingGroupResponse> getByStudentGroupId(UUID studentGroupId) {
        List<BorrowingGroup> borrowingGroups = borrowingGroupRepository.findByStudentGroupId(studentGroupId);
        return borrowingGroups.stream()
                .map(BorrowingGroupMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowingGroupResponse> getByAccountId(UUID accountId) {
        List<BorrowingGroup> borrowingGroups = borrowingGroupRepository.findByAccountId(accountId);
        return borrowingGroups.stream()
                .map(BorrowingGroupMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BorrowingGroupResponse getByStudentGroupAndAccount(UUID studentGroupId, UUID accountId) {
        BorrowingGroup borrowingGroup = borrowingGroupRepository.findByStudentGroupIdAndAccountId(studentGroupId, accountId);
        if (borrowingGroup == null) {
            throw new ResourceNotFoundException("BorrowingGroup not found for student group: " + studentGroupId + " and account: " + accountId);
        }
        return BorrowingGroupMapper.toResponse(borrowingGroup);
    }

    @Override
    public List<BorrowingGroupResponse> getByRole(GroupRoles roles) {
        List<BorrowingGroup> borrowingGroups = borrowingGroupRepository.findByRoles(roles);
        return borrowingGroups.stream()
                .map(BorrowingGroupMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowingGroupResponse> getByStudentGroupAndRole(UUID studentGroupId, GroupRoles roles) {
        List<BorrowingGroup> borrowingGroups = borrowingGroupRepository.findByStudentGroupIdAndRoles(studentGroupId, roles);
        return borrowingGroups.stream()
                .map(BorrowingGroupMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByStudentGroupAndAccount(UUID studentGroupId, UUID accountId) {
        return borrowingGroupRepository.existsByStudentGroupIdAndAccountId(studentGroupId, accountId);
    }

    @Override
    public long countByStudentGroupId(UUID studentGroupId) {
        return borrowingGroupRepository.countByStudentGroupId(studentGroupId);
    }

    @Override
    public List<BorrowingGroupResponse> getByStudentGroupIdWithDetails(UUID studentGroupId) {
        List<BorrowingGroup> borrowingGroups = borrowingGroupRepository.findByStudentGroupIdWithDetails(studentGroupId);
        return borrowingGroups.stream()
                .map(BorrowingGroupMapper::toResponseWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public BorrowingGroupResponse addMemberToGroup(UUID studentGroupId, UUID accountId, GroupRoles roles) {
        BorrowingGroupRequest request = BorrowingGroupRequest.builder()
                .studentGroupId(studentGroupId)
                .accountId(accountId)
                .roles(roles)
                .build();
        return create(request);
    }

    @Override
    public void removeMemberFromGroup(UUID studentGroupId, UUID accountId) {
        BorrowingGroup borrowingGroup = borrowingGroupRepository.findByStudentGroupIdAndAccountId(studentGroupId, accountId);
        if (borrowingGroup == null) {
            throw new ResourceNotFoundException("Member not found in group");
        }
        borrowingGroupRepository.delete(borrowingGroup);
    }

    @Override
    public BorrowingGroupResponse promoteToLeader(UUID studentGroupId, UUID accountId) {
        BorrowingGroup borrowingGroup = borrowingGroupRepository.findByStudentGroupIdAndAccountId(studentGroupId, accountId);
        if (borrowingGroup == null) {
            throw new ResourceNotFoundException("Member not found in group");
        }

        // Demote current leader if exists
        List<BorrowingGroup> currentLeaders = borrowingGroupRepository.findByStudentGroupIdAndRoles(studentGroupId, GroupRoles.LEADER);
        for (BorrowingGroup leader : currentLeaders) {
            leader.setRoles(GroupRoles.MEMBER);
            borrowingGroupRepository.save(leader);
        }

        // Promote new leader
        borrowingGroup.setRoles(GroupRoles.LEADER);
        BorrowingGroup updatedBorrowingGroup = borrowingGroupRepository.save(borrowingGroup);
        return BorrowingGroupMapper.toResponse(updatedBorrowingGroup);
    }

    @Override
    public BorrowingGroupResponse demoteToMember(UUID studentGroupId, UUID accountId) {
        BorrowingGroup borrowingGroup = borrowingGroupRepository.findByStudentGroupIdAndAccountId(studentGroupId, accountId);
        if (borrowingGroup == null) {
            throw new ResourceNotFoundException("Member not found in group");
        }

        borrowingGroup.setRoles(GroupRoles.MEMBER);
        BorrowingGroup updatedBorrowingGroup = borrowingGroupRepository.save(borrowingGroup);
        return BorrowingGroupMapper.toResponse(updatedBorrowingGroup);
    }
}
