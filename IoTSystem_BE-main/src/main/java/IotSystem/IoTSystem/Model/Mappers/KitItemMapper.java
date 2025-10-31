package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Kit_Items;
import IotSystem.IoTSystem.Model.Request.KitItemRequest;
import IotSystem.IoTSystem.Model.Response.KitItemBorrowResponse;
import IotSystem.IoTSystem.Model.Response.KitItemResponse;
import org.springframework.stereotype.Component;

@Component
public class KitItemMapper {

    public Kit_Items toEntity(KitItemRequest request) {
        Kit_Items item = new Kit_Items();
        item.setName(request.getName());
        item.setComponent_Type(request.getComponentType());
        item.setQuantity_total(request.getQuantityTotal());
        item.setQuantity_available(request.getQuantityAvailable());
        item.setPrice(request.getPrice());
        item.setDescription(request.getDescription());
        item.setImageUrl(request.getImageUrl());
        item.setQuantity(request.getQuantity());
        return item;
    }

    public KitItemResponse toResponse(Kit_Items entity) {
        KitItemResponse response = new KitItemResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setComponentType(entity.getComponent_Type());
        response.setQuantityTotal(entity.getQuantity_total());
        response.setQuantityAvailable(entity.getQuantity_available());
        response.setPrice(entity.getPrice());
        response.setDescription(entity.getDescription());
        response.setImageUrl(entity.getImageUrl());
        return response;
    }

    public KitItemBorrowResponse toBorrowResponse(Kit_Items entity) {
        KitItemBorrowResponse response = new KitItemBorrowResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setQuantityAvailable(entity.getQuantity_available());
        response.setImageUrl(entity.getImageUrl());
        return response;
    }
}
