package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Response.BorrowingRequestResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;

public class BorrowingRequestMapper {
    
    public static BorrowingRequestResponse toResponse(BorrowingRequest entity) {
        if (entity == null) return null;
        
        return BorrowingRequestResponse.builder()
                .id(entity.getId())
                .qrCode(entity.getQrCode())
                .status(entity.getStatus())
                .reason(entity.getReason())
                .depositAmount(entity.getDepositAmount())
                .approvedDate(entity.getApprovedDate())
                .expectReturnDate(entity.getExpectReturnDate())
                .actualReturnDate(entity.getActualReturnDate())
                .isLate(entity.getIsLate())
                .note(entity.getNote())
                .requestType(entity.getRequestType())
                .kit(mapKit(entity.getKit()))
                .requestedBy(mapAccount(entity.getRequestedBy()))
//                .penaltyDescription(entity.getPenalties() != null ? entity.getPenalties().get : null)
                .build();
    }
    
    private static KitResponse mapKit(Kits kit) {
        if (kit == null) return null;
        
        return KitResponse.builder()
                .id(kit.getId())
                .kitName(kit.getKitName())
                .type(kit.getType())
                .status(kit.getStatus())
                .description(kit.getDescription())
                .imageUrl(kit.getImageUrl())
                .quantityTotal(kit.getQuantityTotal())
                .quantityAvailable(kit.getQuantityAvailable())
                .amount(kit.getAmount())
                .build();
    }
    
    private static BorrowingRequestResponse.AccountInfo mapAccount(Account account) {
        if (account == null) return null;
        
        return BorrowingRequestResponse.AccountInfo.builder()
                .id(account.getId())
                .email(account.getEmail())
                .fullName(account.getFullName())
                .phone(account.getPhone())
                .build();
    }
}

