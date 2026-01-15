package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.BorrowingRequest;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Model.Response.BorrowingRequestResponse;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import IotSystem.IoTSystem.Model.Entities.Enum.RequestType;
import java.util.stream.Collectors;

public class BorrowingRequestMapper {

    public static BorrowingRequestResponse toResponse(BorrowingRequest entity) {
        if (entity == null) return null;

        KitResponse kitResponse = mapKit(entity.getKit());

        // If BORROW_COMPONENT, populate components from RequestKitComponent
        if (entity.getRequestType() == RequestType.BORROW_COMPONENT && entity.getRequestKitComponents() != null) {
            if (kitResponse == null) {
                kitResponse = KitResponse.builder()
                        .kitName("Component Order")
                        .build();
            }
            kitResponse.setComponents(
                    entity.getRequestKitComponents().stream()
                            .map(rkc -> KitComponentResponse.builder()
                                    .id(rkc.getKitComponentsId())
                                    .componentName(rkc.getComponentName())
                                    .quantityAvailable(rkc.getQuantity())
                                    .build())
                            .collect(java.util.stream.Collectors.toList())
            );

            kitResponse.setKitName(kitResponse.getComponents().get(0).getComponentName());
        }

        return BorrowingRequestResponse.builder()
                .id(entity.getId())
                .qrCode(entity.getQrCode())
                .status(entity.getStatus())
                .reason(entity.getReason())
                .depositAmount(entity.getDepositAmount())
                .createdAt(entity.getCreatedAt())
                .approvedDate(entity.getApprovedDate())
                .expectReturnDate(entity.getExpectReturnDate())
                .actualReturnDate(entity.getActualReturnDate())
                .isLate(entity.getIsLate())
                .note(entity.getNote())
                .requestType(entity.getRequestType())
                .kit(kitResponse)
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
                .studentCode(account.getStudentCode())
                .lecturerCode(account.getLecturerCode())
                .role(account.getRole() != null ? account.getRole().getName() : null)
                .build();
    }
}

