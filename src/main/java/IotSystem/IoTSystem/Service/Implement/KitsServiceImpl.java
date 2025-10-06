package IotSystem.IoTSystem.Service.Implement;


import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Mappers.KitMapper;
import IotSystem.IoTSystem.Model.Mappers.KitResponseMapper;
import IotSystem.IoTSystem.Model.Request.KitCreationRequest;
import IotSystem.IoTSystem.Model.Request.KitRequest;
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
        return List.of();
    }

    @Override
    public KitResponse updateKit(UUID kitId, KitRequest request) {
        return null;
    }

    @Override
    public void deleteKit(UUID kitId) {

    }

    @Override
    public List<KitComponentResponse> getComponentsByKitId(UUID kitId) {
        return List.of();
    }
}
