package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Entities.Kit_Component;
import IotSystem.IoTSystem.Repository.KitComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service

public class KitComponentService {

    @Autowired
    private KitComponentRepository repository;

    public List<Kit_Component> getAll() {
        return repository.findAll();
    }

    public Kit_Component getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public Kit_Component create(Kit_Component component) {
        component.setCreatedAt(new Date());
        component.setUpdatedAt(new Date());
        return repository.save(component);
    }

    public Kit_Component update(UUID id, Kit_Component updated) {
        Kit_Component existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setComponentName(updated.getComponentName());
            existing.setComponentType(updated.getComponentType());
            existing.setDescription(updated.getDescription());
            existing.setUnit(updated.getUnit());
            existing.setQuantityTotal(updated.getQuantityTotal());
            existing.setQuantityAvailable(updated.getQuantityAvailable());
            existing.setUnitPrice(updated.getUnitPrice());
            existing.setTotalValue(updated.getTotalValue());
            existing.setStatus(updated.getStatus());
            existing.setLocation(updated.getLocation());
            existing.setImageUrl(updated.getImageUrl());
            existing.setLastCheckedDate(updated.getLastCheckedDate());
            existing.setUpdatedAt(new Date());
            existing.setKit(updated.getKit());
            return repository.save(existing);
        }
        return null;
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
