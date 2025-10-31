package IotSystem.IoTSystem.Service.Implement;


import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Mappers.KitMapper;
import IotSystem.IoTSystem.Model.Mappers.KitResponseMapper;
import IotSystem.IoTSystem.Model.Request.*;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import IotSystem.IoTSystem.Repository.KitComponentRepository;
import IotSystem.IoTSystem.Repository.KitsRepository;
import IotSystem.IoTSystem.Service.IKitsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KitsServiceImpl implements IKitsService {

    @Autowired
    private final KitsRepository kitRepository;

    @Autowired
    private final KitComponentRepository kitComponentRepository;


    @Override
    public KitResponse createKitWithComponents(KitCreationRequest request) {
        // Chuyển request thành entity
        Kits kit = KitMapper.toKitEntity(request);
        Kits savedKit = kitRepository.save(kit);
        // Tạo các component
        List<Kit_Component> components = KitMapper.toComponentEntities(request.getComponents(), savedKit);
        kitComponentRepository.saveAll(components);
        
        // Calculate kit amount = sum of all component amounts
        float kitAmount = (float) components.stream()
                .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                .sum();
        savedKit.setAmount(kitAmount);
        kitRepository.save(savedKit);
        
        // Trả về response DTO
        return KitResponseMapper.toResponse(savedKit, components);
    }





    @Override
    public KitResponse getKitById(UUID kitId) {
        Kits kit = kitRepository.findById(kitId)
                .orElseThrow(() -> new RuntimeException("Kit not found"));

        List<Kit_Component> components = kitComponentRepository.findByKitId(kitId);

        return KitResponseMapper.toResponse(kit, components);
    }


    @Override
    public List<KitResponse> getAllKits() {
        List<Kits> kits = kitRepository.findAll();
        return kits.stream()
                .map(kit -> KitResponseMapper.mapKitsDto(kit, kit.getComponents()))
                .collect(Collectors.toList());
    }

    @Override
    public List<KitResponse> getAllKitsForStudent(){
        List<Kits> kits = kitRepository.findByType(KitType.STUDENT_KIT);
        return kits.stream().map(
                kit -> KitResponseMapper.mapKitsDto(kit, kit.getComponents())).collect(Collectors.toList());
    }

    @Override
    public KitResponse updateKit(UUID kitId, KitRequest request) {
        Kits kit = kitRepository.findById(kitId).orElseThrow(() -> new ResourceNotFoundException("Did not found Kit ID: " + kitId));
        KitMapper.updateKit(kit, request);
        Kits updating = kitRepository.save(kit);
        return KitResponseMapper.toResponse(updating, updating.getComponents());
    }

    @Override
    public void deleteKit(UUID kitId) {

    }

    @Override
    public List<KitComponentResponse> getComponentsByKitId(UUID kitId) {
        return List.of();
    }

    @Override
    public KitResponse createSingleKit(KitSingleCreateRequest request) {
        Kits kits = KitMapper.toKitSingleEntity(request);
        Kits savedKit = kitRepository.save(kits);
        return KitResponseMapper.toResponse(savedKit, List.of());
    }

    @Override
    public KitComponentResponse addSingleComponentToKit(AddSingleComponentRequest request) {
        Kits kit = kitRepository.findById(request.getKitId())
                .orElseThrow(() -> new RuntimeException("Kit not found"));

        Kit_Component component = KitMapper.toComponentEntity(request.getComponent(), kit);
        Kit_Component saved = kitComponentRepository.save(component);

        // Recalculate kit amount after adding component
        List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
        float kitAmount = (float) allComponents.stream()
                .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                .sum();
        kit.setAmount(kitAmount);
        kitRepository.save(kit);

        return KitResponseMapper.toComponentResponse(saved);
    }

    @Override
    public List<KitComponentResponse> addMultipleComponentsToKit(AddMultipleComponentsRequest request) {
        Kits kit = kitRepository.findById(request.getKitId())
                .orElseThrow(() -> new RuntimeException("Kit not found"));

        List<Kit_Component> components = request.getComponents().stream()
                .map(req -> KitMapper.toComponentEntity(req, kit))
                .collect(Collectors.toList());

        List<Kit_Component> savedList = kitComponentRepository.saveAll(components);

        // Recalculate kit amount after adding components
        List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
        float kitAmount = (float) allComponents.stream()
                .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                .sum();
        kit.setAmount(kitAmount);
        kitRepository.save(kit);

        return KitResponseMapper.toComponentResponseList(savedList);
    }


}
