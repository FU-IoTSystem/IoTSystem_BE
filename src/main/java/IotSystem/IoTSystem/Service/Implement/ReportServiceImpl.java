package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.DamageReport;
import IotSystem.IoTSystem.Service.IReportService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReportServiceImpl implements IReportService {
    @Override
    public List<DamageReport> getAll() {
        return List.of();
    }

    @Override
    public DamageReport getById(UUID id) {
        return null;
    }

    @Override
    public DamageReport create(DamageReport report) {
        return null;
    }

    @Override
    public List<DamageReport> getByAccount(UUID accountId) {
        return List.of();
    }

    @Override
    public DamageReport update(UUID id, DamageReport report) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
