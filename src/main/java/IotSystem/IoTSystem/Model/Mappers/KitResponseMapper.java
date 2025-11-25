package IotSystem.IoTSystem.Model.Mappers;


import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        // Chỉ set tổng pricePerCom
        float totalAmount = 0.0f;
        if (kit.getComponents() != null) {
            for (Kit_Component comp : kit.getComponents()) {
                if (comp.getPricePerCom() != null) {
                    totalAmount += comp.getPricePerCom();
                }
            }
        }
        response.setAmount(totalAmount);

        List<KitComponentResponse> componentResponses = components.stream().map(comp -> {
            KitComponentResponse cr = new KitComponentResponse();
            cr.setId(comp.getId());
            cr.setComponentName(comp.getComponentName());
            cr.setComponentType(comp.getComponentType());
            cr.setDescription(comp.getDescription());
            cr.setQuantityTotal(comp.getQuantityTotal());
            cr.setQuantityAvailable(comp.getQuantityAvailable());
            cr.setPricePerCom(comp.getPricePerCom());
            // Không còn amount; FE lấy pricePerCom
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
        // Không còn amount; FE lấy pricePerCom
        cr.setStatus(comp.getStatus());
        cr.setImageUrl(comp.getImageUrl());
        return cr;
    }

    public static List<KitComponentResponse> toComponentResponseList(List<Kit_Component> components) {
        return components.stream()
                .map(KitResponseMapper::toComponentResponse)
                .collect(Collectors.toList());
    }
    public static List<KitResponse> mapperListKits(List<Kits> kits, Map<UUID, List<Kit_Component>> kitComponentsByKitId) {
        return kits.stream().map(kit -> mapKitsDto(kit, kitComponentsByKitId.getOrDefault(kit.getId(), List.of()))).toList();
    }
    public static KitResponse mapKitsDto(Kits kits, List<Kit_Component> kitComponents){
        KitResponse kit = KitResponse.builder()
                .kitName(kits.getKitName())
                .id(kits.getId())
                .type(kits.getType())
                .description(kits.getDescription())
                .imageUrl(kits.getImageUrl())
                .status(kits.getStatus())
                .quantityAvailable(kits.getQuantityAvailable())
                .quantityTotal(kits.getQuantityTotal())
                .amount(kits.getAmount())
                .build();
        List<KitComponentResponse> componentResponses = kitComponents.stream().map(comp -> {
            KitComponentResponse cr = new KitComponentResponse();
            cr.setId(comp.getId());
            cr.setComponentName(comp.getComponentName());
            cr.setComponentType(comp.getComponentType());
            cr.setDescription(comp.getDescription());
            cr.setQuantityTotal(comp.getQuantityTotal());
            cr.setQuantityAvailable(comp.getQuantityAvailable());
            cr.setPricePerCom(comp.getPricePerCom());
            // Không còn amount; FE lấy pricePerCom
            cr.setStatus(comp.getStatus());
            cr.setImageUrl(comp.getImageUrl());
            return cr;
        }).collect(Collectors.toList());

        kit.setComponents(componentResponses);

        return kit;
    }
}

