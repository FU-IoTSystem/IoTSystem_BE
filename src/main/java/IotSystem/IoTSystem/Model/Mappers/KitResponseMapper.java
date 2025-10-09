package IotSystem.IoTSystem.Model.Mappers;


import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;

import java.util.List;
import java.util.stream.Collectors;

public class KitResponseMapper {

    public static KitResponse toResponse(Kits kit, List<Kit_Component> components) {
        KitResponse response = new KitResponse();
        response.setId(kit.getId());
        response.setKitName(kit.getKitName());
        response.setType(kit.getType());
        response.setStatus(kit.getStatus());
        response.setDescription(kit.getDescription());
        response.setImageUrl(kit.getImageUrl());
        response.setQuantityTotal(kit.getQuantityTotal());
        response.setQuantityAvailable(kit.getQuantityAvailable());

        List<KitComponentResponse> componentResponses = components.stream().map(comp -> {
            KitComponentResponse cr = new KitComponentResponse();
            cr.setId(comp.getId());
            cr.setComponentName(comp.getComponentName());
            cr.setComponentType(comp.getComponentType());
            cr.setDescription(comp.getDescription());
            cr.setQuantityTotal(comp.getQuantityTotal());
            cr.setQuantityAvailable(comp.getQuantityAvailable());
            cr.setPricePerCom(comp.getPricePerCom());
            cr.setStatus(comp.getStatus());
            cr.setImageUrl(comp.getImageUrl());
            return cr;
        }).collect(Collectors.toList());

        response.setComponents(componentResponses);
        return response;
    }
    public static KitComponentResponse toComponentResponse(Kit_Component comp) {
        KitComponentResponse cr = new KitComponentResponse();
        cr.setId(comp.getId());
        cr.setComponentName(comp.getComponentName());
        cr.setComponentType(comp.getComponentType());
        cr.setDescription(comp.getDescription());
        cr.setQuantityTotal(comp.getQuantityTotal());
        cr.setQuantityAvailable(comp.getQuantityAvailable());
        cr.setPricePerCom(comp.getPricePerCom());
        cr.setStatus(comp.getStatus());
        cr.setImageUrl(comp.getImageUrl());
        return cr;
    }

    public static List<KitComponentResponse> toComponentResponseList(List<Kit_Component> components) {
        return components.stream()
                .map(KitResponseMapper::toComponentResponse)
                .collect(Collectors.toList());
    }

}

