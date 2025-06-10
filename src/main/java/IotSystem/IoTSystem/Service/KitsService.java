package IotSystem.IoTSystem.Service;


import IotSystem.IoTSystem.Entities.Kits;
import IotSystem.IoTSystem.Repository.KitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KitsService {



    @Autowired
    private KitsRepository kitsRepository;

    public List<Kits> getAllKits() {
        return kitsRepository.findAll();
    }

    public Kits getKitById(Integer id) {
        return kitsRepository.findById(id).orElse(null);
    }

    //hàm update bằng id , tìm coi có tồn tại hay chưa và thay thế
    public String updateKit(Integer id, Kits updatedKit) {
        Kits existingKit = kitsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kit not found with id: " + id));

        existingKit.setType(updatedKit.getType());
        existingKit.setStatus(updatedKit.getStatus());
        existingKit.setQrCode(updatedKit.getQrCode());
        existingKit.setDescription(updatedKit.getDescription());

         kitsRepository.save(existingKit);

    return "Kit updated";
    }

    public String deleteKit(Integer id) {
        Optional<Kits> existingKit = kitsRepository.findById(id);
        if (existingKit.isPresent()) {
            Kits kitToDelete = existingKit.get();
            kitsRepository.delete(kitToDelete);
            return "Kit with ID " + id + " deleted successfully";
        }
        return "Kit with ID" + id + " not found";
    }




}
