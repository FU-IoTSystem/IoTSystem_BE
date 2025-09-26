package IotSystem.IoTSystem.DTOs.Mappers;

import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Request.KitComponentRequest;
import IotSystem.IoTSystem.Model.Response.KitComponentBorrowResponse;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
import org.springframework.stereotype.Component;

@Component
public class KitComponentMapper {

    public Kit_Component toEntity(KitComponentRequest request, Kits kit) {
        return Kit_Component.builder()
                .componentName(request.getComponentName())
                .componentType(request.getComponentType())
                .description(request.getDescription())
                .quantityTotal(request.getQuantityTotal())
                .quantityAvailable(request.getQuantityAvailable())
                .pricePerCom(request.getPricePerCom())
                .status(request.getStatus())
                .imageUrl(request.getImageUrl())
                .kit(kit)
                .build();
    }

    public KitComponentResponse toResponse(Kit_Component entity) {
        KitComponentResponse response = new KitComponentResponse();
        response.setId(entity.getId());
        response.setComponentName(entity.getComponentName());
        response.setComponentType(entity.getComponentType());
        response.setDescription(entity.getDescription());
        response.setQuantityTotal(entity.getQuantityTotal());
        response.setQuantityAvailable(entity.getQuantityAvailable());
        response.setPricePerCom(entity.getPricePerCom());
        response.setStatus(entity.getStatus());
        response.setImageUrl(entity.getImageUrl());
        if (entity.getKit() != null) {
            response.setKitId(entity.getKit().getId());
            response.setKitName(entity.getKit().getKitName());
        }
        return response;
    }

    public KitComponentBorrowResponse toBorrowResponse(Kit_Component entity) {
        KitComponentBorrowResponse response = new KitComponentBorrowResponse();
        response.setId(entity.getId());
        response.setComponentName(entity.getComponentName());
        response.setDescription(entity.getDescription());
        response.setQuantityAvailable(entity.getQuantityAvailable());
        response.setImageUrl(entity.getImageUrl());
        return response;
    }
}
