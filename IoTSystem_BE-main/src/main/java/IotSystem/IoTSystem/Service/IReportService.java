package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.DamageReport;

import java.util.List;
import java.util.UUID;

public interface IReportService {
    List<DamageReport> getAll();

    DamageReport getById(UUID id);

    DamageReport create(DamageReport report);

    List<DamageReport> getByAccount(UUID accountId);

    DamageReport update(UUID id, DamageReport report);

    void delete(UUID id);
}
