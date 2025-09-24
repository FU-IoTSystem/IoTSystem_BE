package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.Report;

import java.util.List;
import java.util.UUID;

public interface IReportService {
    List<Report> getAll();

    Report getById(UUID id);

    Report create(Report report);

    List<Report> getByAccount(UUID accountId);

    Report update(UUID id, Report report);

    void delete(UUID id);
}
