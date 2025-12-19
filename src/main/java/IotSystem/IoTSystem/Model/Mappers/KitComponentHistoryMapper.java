package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.KitComponentHistory;
import IotSystem.IoTSystem.Model.Response.KitComponentHistoryResponse;

public class KitComponentHistoryMapper {

    private KitComponentHistoryMapper() {}

    public static KitComponentHistoryResponse toResponse(KitComponentHistory history) {
        if (history == null) {
            return null;
        }

        return KitComponentHistoryResponse.builder()
                .id(history.getId())
                .kitId(history.getKit() != null ? history.getKit().getId() : null)
                .kitName(history.getKit() != null ? history.getKit().getKitName() : null)
                .componentId(history.getComponent() != null ? history.getComponent().getId() : null)
                .componentName(history.getComponent() != null ? history.getComponent().getComponentName() : null)
                .action(history.getAction())
                .oldStatus(history.getOldStatus())
                .newStatus(history.getNewStatus())
                .note(history.getNote())
                .penaltyDetailId(history.getPenaltyDetail() != null ? history.getPenaltyDetail().getId() : null)
                .penaltyDetailImageUrl(history.getPenaltyDetail() != null ? history.getPenaltyDetail().getImageUrl() : null)
                .createdAt(history.getCreatedAt())
                .build();
    }
}

