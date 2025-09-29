package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Request.KitRequest;
import IotSystem.IoTSystem.Model.Response.KitBorrowResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import org.springframework.stereotype.Component;

@Component
public class KitMapper {

    public Kits toEntity(KitRequest request) {
        Kits kit = new Kits();
        kit.setKitName(request.getKitName());
        kit.setType(request.getType());
        kit.setStatus(request.getStatus());
        kit.setDescription(request.getDescription());
        kit.setImageUrl(request.getImageUrl());
        kit.setQuantityTotal(request.getQuantityTotal());
        kit.setQuantityAvailable(request.getQuantityAvailable());
        return kit;
    }

    public KitResponse toResponse(Kits entity) {
        KitResponse response = new KitResponse();
        response.setId(entity.getId());
        response.setKitName(entity.getKitName());
        response.setType(entity.getType());
        response.setStatus(entity.getStatus());
        response.setDescription(entity.getDescription());
        response.setImageUrl(entity.getImageUrl());
        response.setQuantityTotal(entity.getQuantityTotal());
        response.setQuantityAvailable(entity.getQuantityAvailable());
        return response;
    }

    public KitBorrowResponse toBorrowResponse(Kits entity) {
        KitBorrowResponse response = new KitBorrowResponse();
        response.setId(entity.getId());
        response.setKitName(entity.getKitName());
        response.setDescription(entity.getDescription());
        response.setImageUrl(entity.getImageUrl());
        response.setQuantityAvailable(entity.getQuantityAvailable());
        return response;
    }
}