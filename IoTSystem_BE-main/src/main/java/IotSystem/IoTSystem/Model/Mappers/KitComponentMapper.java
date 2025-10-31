package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Request.KitComponentRequest;
import IotSystem.IoTSystem.Model.Response.KitComponentBorrowResponse;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class  KitComponentMapper {

    public static Kit_Component toEntity(KitComponentRequest request, Kits kit) {
        Kit_Component entity = new Kit_Component();
        entity.setComponentName(request.getComponentName());
        entity.setComponentType(request.getComponentType());
        entity.setDescription(request.getDescription());
        entity.setQuantityTotal(request.getQuantityTotal());
        entity.setQuantityAvailable(request.getQuantityAvailable());
        entity.setPricePerCom(request.getPricePerCom());
        entity.setStatus(request.getStatus());
        entity.setImageUrl(request.getImageUrl());
        entity.setKit(kit);
        return entity;
    }

    public static void updateEntity(KitComponentRequest request, Kit_Component existingEntity, Kits kit) {
        if (request.getComponentName() != null) {
            existingEntity.setComponentName(request.getComponentName());
        }
        if (request.getComponentType() != null) {
            existingEntity.setComponentType(request.getComponentType());
        }
        if (request.getDescription() != null) {
            existingEntity.setDescription(request.getDescription());
        }
        if (request.getQuantityTotal() != null) {
            existingEntity.setQuantityTotal(request.getQuantityTotal());
        }
        if (request.getQuantityAvailable() != null) {
            existingEntity.setQuantityAvailable(request.getQuantityAvailable());
        }
        if (request.getPricePerCom() != null) {
            existingEntity.setPricePerCom(request.getPricePerCom());
        }
        if (request.getStatus() != null) {
            existingEntity.setStatus(request.getStatus());
        }
        if (request.getImageUrl() != null) {
            existingEntity.setImageUrl(request.getImageUrl());
        }
        if (kit != null) {
            existingEntity.setKit(kit);
        }
    }

    public static KitComponentResponse toResponse(Kit_Component entity) {
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
