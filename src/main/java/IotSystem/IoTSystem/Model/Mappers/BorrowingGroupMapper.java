package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.BorrowingGroup;
import IotSystem.IoTSystem.Model.Entities.StudentGroup;
import IotSystem.IoTSystem.Model.Request.BorrowingGroupRequest;
import IotSystem.IoTSystem.Model.Response.BorrowingGroupResponse;
import org.springframework.stereotype.Component;

@Component
public class BorrowingGroupMapper {

    public static BorrowingGroup toEntity(BorrowingGroupRequest request, StudentGroup studentGroup, Account account) {
        BorrowingGroup borrowingGroup = new BorrowingGroup();
        borrowingGroup.setRoles(request.getRoles());
        borrowingGroup.setStudentGroup(studentGroup);
        borrowingGroup.setAccount(account);
        return borrowingGroup;
    }

    public static BorrowingGroupResponse toResponse(BorrowingGroup entity) {
        if (entity == null) return null;

        return BorrowingGroupResponse.builder()
                .id(entity.getId())
                .roles(entity.getRoles())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .studentGroupId(entity.getStudentGroup() != null ? entity.getStudentGroup().getId() : null)
                .studentGroupName(entity.getStudentGroup() != null ? entity.getStudentGroup().getGroupName() : null)
                .classId(entity.getStudentGroup() != null && entity.getStudentGroup().getClazz() != null 
                        ? entity.getStudentGroup().getClazz().getId() : null)
                .className(entity.getStudentGroup() != null && entity.getStudentGroup().getClazz() != null 
                        ? entity.getStudentGroup().getClazz().getClassCode() : null)
                .accountId(entity.getAccount() != null ? entity.getAccount().getId() : null)
                .accountName(entity.getAccount() != null ? entity.getAccount().getFullName() : null)
                .accountEmail(entity.getAccount() != null ? entity.getAccount().getEmail() : null)
                .accountPhone(entity.getAccount() != null ? entity.getAccount().getPhone() : null)
                .studentCode(entity.getAccount() != null ? entity.getAccount().getStudentCode() : null)
                .build();
    }

    public static BorrowingGroupResponse toResponseWithDetails(BorrowingGroup entity) {
        return toResponse(entity);
    }
}
