package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Service.PenaltyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PenaltyServiceImpl implements PenaltyService {
    @Override
    public List<Penalty> getAll() {
        return List.of();
    }

    @Override
    public Penalty getById(UUID id) {
        return null;
    }

    @Override
    public Penalty create(Penalty penalty) {
        return null;
    }

    @Override
    public Penalty update(UUID id, Penalty penalty) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
