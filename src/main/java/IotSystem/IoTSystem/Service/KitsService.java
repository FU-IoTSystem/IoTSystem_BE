package IotSystem.IoTSystem.Service;


import IotSystem.IoTSystem.DTOs.Mappers.KitMapper;
import IotSystem.IoTSystem.DTOs.Response.KitResponseDTO;
import IotSystem.IoTSystem.Entities.Kits;
import IotSystem.IoTSystem.Repository.KitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KitsService {


    @Autowired
    private KitsRepository kitsRepository;

    public List<KitResponseDTO> getAllKits() {
        return kitsRepository.findAll()
                .stream()
                .map(KitMapper::toDTO)
                .collect(Collectors.toList());
    }

    public KitResponseDTO getKitById(Integer id) {
        Kits kit = kitsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kit not found with id: " + id));
        return KitMapper.toDTO(kit);
    }

    public KitResponseDTO updateKit(Integer id, Kits updatedKit) {
        Kits existingKit = kitsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kit not found with id: " + id));

        existingKit.setType(updatedKit.getType());
        existingKit.setStatus(updatedKit.getStatus());
        existingKit.setQrCode(updatedKit.getQrCode());
        existingKit.setDescription(updatedKit.getDescription());

        Kits savedKit = kitsRepository.save(existingKit);
        return KitMapper.toDTO(savedKit);
    }

    public void deleteKit(Integer id) {
        Kits kit = kitsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kit not found with id: " + id));
        kitsRepository.delete(kit);
    }



}
