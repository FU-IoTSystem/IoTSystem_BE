package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Request.KitComponentRequest;
import IotSystem.IoTSystem.Model.Request.KitCreationRequest;
import IotSystem.IoTSystem.Model.Request.KitRequest;
import IotSystem.IoTSystem.Model.Request.KitSingleCreateRequest;
import IotSystem.IoTSystem.Model.Response.KitBorrowResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class KitMapper {

    public static Kits toKitEntity(KitCreationRequest request) {
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

    public static List<Kit_Component> toComponentEntities(List<KitComponentRequest> componentRequests, Kits kit) {
        return componentRequests.stream().map(req -> {
            Kit_Component component = new Kit_Component();
            component.setComponentName(req.getComponentName());
            component.setComponentType(req.getComponentType());
            component.setDescription(req.getDescription());
            component.setQuantityTotal(req.getQuantityTotal());
            component.setQuantityAvailable(req.getQuantityAvailable());
            component.setPricePerCom(req.getPricePerCom());
            component.setStatus(req.getStatus());
            component.setImageUrl(req.getImageUrl());
            component.setKit(kit);
            return component;
        }).collect(Collectors.toList());
    }
    public static Kits toKitSingleEntity(KitSingleCreateRequest request) {
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

    public static Kit_Component toComponentEntity(KitComponentRequest req, Kits kit) {
        Kit_Component component = new Kit_Component();
        component.setComponentName(req.getComponentName());
        component.setComponentType(req.getComponentType());
        component.setDescription(req.getDescription());
        component.setQuantityTotal(req.getQuantityTotal());
        component.setQuantityAvailable(req.getQuantityAvailable());
        component.setPricePerCom(req.getPricePerCom());
        component.setStatus(req.getStatus());
        component.setImageUrl(req.getImageUrl());
        component.setKit(kit);
        return component;
    }

}