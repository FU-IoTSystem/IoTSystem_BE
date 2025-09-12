package IotSystem.IoTSystem.Service;


import IotSystem.IoTSystem.Entities.Kits;
import IotSystem.IoTSystem.Repository.KitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class KitsService {

    @Autowired
    private KitsRepository kitsRepository;

    public List<Kits> getAll() {
        return kitsRepository.findAll();
    }

    public Kits getById(UUID id) {
        return kitsRepository.findById(id).orElse(null);
    }

    public Kits create(Kits kit) {
        kit.setCreatedAt(new Date());
        kit.setUpdatedAt(new Date());
        return kitsRepository.save(kit);
    }

    public Kits update(UUID id, Kits updated) {
        Kits existing = kitsRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setKitName(updated.getKitName());
            existing.setType(updated.getType());
            existing.setStatus(updated.getStatus());
            existing.setQrCode(updated.getQrCode());
            existing.setDescription(updated.getDescription());
            existing.setLocation(updated.getLocation());
            existing.setRefNumber(updated.getRefNumber());
            existing.setImageUrl(updated.getImageUrl());
            existing.setLastMaintenanceDate(updated.getLastMaintenanceDate());
            existing.setUpdatedAt(new Date());
            return kitsRepository.save(existing);
        }
        return null;
    }

    public void delete(UUID id) {
        kitsRepository.deleteById(id);
    }

}
