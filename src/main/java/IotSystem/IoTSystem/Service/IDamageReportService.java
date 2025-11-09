package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.DamageReport;
import IotSystem.IoTSystem.Model.Request.DamageReportRequest;

import java.util.List;
import java.util.UUID;

public interface IDamageReportService {
    List<DamageReport> getAll();

    DamageReport getById(UUID id);

    DamageReport create(DamageReportRequest request);

    DamageReport update(UUID id, DamageReportRequest request);

    void delete(UUID id);

    List<DamageReport> getByAccountId(UUID accountId);

    List<DamageReport> getByKitId(UUID kitId);

    List<DamageReport> getByBorrowRequestId(UUID borrowRequestId);

    List<DamageReport> getByStatus(String status);
}