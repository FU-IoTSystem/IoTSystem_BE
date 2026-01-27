package IotSystem.IoTSystem.Model.Mappers;

import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Request.KitComponentRequest;
import IotSystem.IoTSystem.Model.Request.KitCreationRequest;
import IotSystem.IoTSystem.Model.Request.KitRequest;
import IotSystem.IoTSystem.Model.Request.KitSingleCreateRequest;
import IotSystem.IoTSystem.Model.Response.KitBorrowResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
        // Validate quantity fields before setting
        Integer quantityTotal = request.getQuantityTotal();
        Integer quantityAvailable = request.getQuantityAvailable();
        validateQuantities(quantityTotal, quantityAvailable);
        kit.setQuantityTotal(quantityTotal);
        kit.setQuantityAvailable(quantityAvailable);
        return kit;
    }

    public static void updateKit(Kits existingKit, KitRequest request){
        existingKit.setKitName(request.getKitName());
        existingKit.setDescription(request.getDescription());
        existingKit.setStatus(request.getStatus());
        existingKit.setType(KitType.valueOf(request.getType()));
        // Calculate effective quantities (existing value if null in request)
        Integer newQuantityTotal = request.getQuantityTotal() != null
                ? request.getQuantityTotal()
                : existingKit.getQuantityTotal();
        Integer newQuantityAvailable = request.getQuantityAvailable() != null
                ? request.getQuantityAvailable()
                : existingKit.getQuantityAvailable();

        // Validate quantity relationship before applying changes
        validateQuantities(newQuantityTotal, newQuantityAvailable);

        // Only update if request has new values
        if (request.getQuantityTotal() != null) {
            existingKit.setQuantityTotal(request.getQuantityTotal());
        }
        if (request.getQuantityAvailable() != null) {
            existingKit.setQuantityAvailable(request.getQuantityAvailable());
        }
        if (request.getAmount() != null) {
            existingKit.setAmount(request.getAmount());
        }
        existingKit.setImageUrl(request.getImageUrl());
        existingKit.setUpdatedAt(LocalDateTime.now());
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
            // Calculate amount = pricePerCom * quantityTotal
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
        // Validate quantity fields before setting
        Integer quantityTotal = request.getQuantityTotal();
        Integer quantityAvailable = request.getQuantityAvailable();
        validateQuantities(quantityTotal, quantityAvailable);
        kit.setQuantityTotal(quantityTotal);
        kit.setQuantityAvailable(quantityAvailable);
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
        // Calculate amount = pricePerCom * quantityTotal
        component.setStatus(req.getStatus());
        component.setImageUrl(req.getImageUrl());
        component.setKit(kit);
        return component;
    }

    /**
     * Validate that quantityTotal and quantityAvailable have a consistent relationship.
     * - Both must be null or non-negative when provided
     * - quantityAvailable must not be greater than quantityTotal
     */
    private static void validateQuantities(Integer quantityTotal, Integer quantityAvailable) {
        if (quantityTotal != null && quantityTotal < 0) {
            throw new IllegalArgumentException("quantityTotal cannot be negative");
        }
        if (quantityAvailable != null && quantityAvailable < 0) {
            throw new IllegalArgumentException("quantityAvailable cannot be negative");
        }
        if (quantityTotal != null && quantityAvailable != null && quantityAvailable > quantityTotal) {
            throw new IllegalArgumentException("quantityAvailable cannot be greater than quantityTotal");
        }
    }

}