package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Response.ProfileResponse;

public class AccountMapper {

    public static ProfileResponse toProfileResponse(Account account) {
        if (account == null) return null;

        String studentCode = account.getRole().getName().equals("STUDENT")
                ? account.getStudentCode()
                : null;

        String lecturerCode = account.getRole().getName().equals("LECTURER")
                ? account.getLecturerCode()
                : null;

        return new ProfileResponse(
                account.getId(),
                account.getFullName(),
                account.getEmail(),
                account.getAvatarUrl(),
                account.getPhone(),
                studentCode,
                lecturerCode,
                account.getRole().getName(),
                account.getCreatedAt(),
                account.getIsActive()
        );
    }
}
