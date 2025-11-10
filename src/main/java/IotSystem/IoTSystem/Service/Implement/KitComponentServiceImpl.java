package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Mappers.KitComponentMapper;
import IotSystem.IoTSystem.Model.Mappers.KitResponseMapper;
import IotSystem.IoTSystem.Model.Request.KitComponentRequest;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import IotSystem.IoTSystem.Repository.KitComponentRepository;
import IotSystem.IoTSystem.Repository.KitsRepository;
import IotSystem.IoTSystem.Service.IKitComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KitComponentServiceImpl implements IKitComponentService {

    @Autowired
    KitComponentRepository kitComponentRepository;
    @Autowired
    KitsRepository kitsRepository;


    @Override
    public KitComponentResponse createKitComponent(KitComponentRequest kitComponentRequest) {
        Optional<Kits> result = kitsRepository.findById(kitComponentRequest.getKitId());
        if (result.isPresent()) {
            Kits kit = result.get();
            Kit_Component kitComponent = KitComponentMapper.toEntity(kitComponentRequest, kit);
            kitComponentRepository.save(kitComponent);

            // Recalculate kit amount after adding component
            List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
            float kitAmount = (float) allComponents.stream()
                    .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                    .sum();
            kit.setAmount(kitAmount);
            kitsRepository.save(kit);

            return KitComponentMapper.toResponse(kitComponent);
        } else {
            throw new RuntimeException("KitId Not Found");
        }
    }

    @Override
    public Object getKitComponentId(Long id) {
        return null;
    }

    @Override
    public List<KitComponentResponse> getAllKitComponents() {
        return List.of();
    }

    @Override
    public KitResponse deleteKitComponent(UUID id) {
        Kit_Component entity = kitComponentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Kit Component ID not found: " + id));
        Kits kit = entity.getKit();
        kitComponentRepository.delete(entity);

        // Recalculate kit amount after deleting component
        List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
        float kitAmount = (float) allComponents.stream()
                .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                .sum();
        kit.setAmount(kitAmount);
        kitsRepository.save(kit);

        return KitResponseMapper.toResponse(kit, kit.getComponents());
    }

    @Override
    public KitComponentResponse updateKitComponent(UUID id, KitComponentRequest kitComponentRequest) {
        Kit_Component component = kitComponentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Kit Component ID not found: " + id));

        KitComponentMapper.updateEntity(kitComponentRequest, component, component.getKit());
        Kit_Component updatedComponent = kitComponentRepository.save(component);

        // Recalculate kit amount after updating component
        Kits kit = updatedComponent.getKit();
        List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
        float kitAmount = (float) allComponents.stream()
                .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                .sum();
        kit.setAmount(kitAmount);
        kitsRepository.save(kit);

        return KitComponentMapper.toResponse(updatedComponent);
    }
}
