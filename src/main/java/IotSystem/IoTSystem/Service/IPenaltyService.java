package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.Penalty;

import java.util.List;
import java.util.UUID;

public interface IPenaltyService {
    List<Penalty> getAll();

    Penalty getById(UUID id);

    Penalty create(Penalty penalty);

    Penalty update(UUID id, Penalty penalty);

    void delete(UUID id);
}
