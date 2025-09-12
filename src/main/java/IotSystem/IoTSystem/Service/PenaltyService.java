package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Entities.Penalty;
import IotSystem.IoTSystem.Repository.PenaltyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service

public class PenaltyService {
    @Autowired
    private PenaltyRepository penaltyRepository;

    public List<Penalty> getAll() {
        return penaltyRepository.findAll();
    }

    public Penalty getById(UUID id) {
        return penaltyRepository.findById(id).orElse(null);
    }

    public Penalty create(Penalty penalty) {
        penalty.setEffectiveDate(new Date());
        return penaltyRepository.save(penalty);
    }

    public Penalty update(UUID id, Penalty updated) {
        Penalty existing = penaltyRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setCourseName(updated.getCourseName());
            existing.setDepositAmount(updated.getDepositAmount());
            existing.setPenaltyAmount(updated.getPenaltyAmount());
            existing.setPenaltyPerDay(updated.getPenaltyPerDay());
            existing.setDamagePenalty(updated.getDamagePenalty());
            existing.setLostPenalty(updated.getLostPenalty());
            existing.setKitType(updated.getKitType());
            existing.setEffectiveDate(updated.getEffectiveDate());
            existing.setExpiryDate(updated.getExpiryDate());
            existing.setRequest(updated.getRequest());
            existing.setAccount(updated.getAccount());
            return penaltyRepository.save(existing);
        }
        return null;
    }

    public void delete(UUID id) {
        penaltyRepository.deleteById(id);
    }
}
