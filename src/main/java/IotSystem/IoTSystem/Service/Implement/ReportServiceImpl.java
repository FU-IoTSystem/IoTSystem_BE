package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.Report;
import IotSystem.IoTSystem.Service.ReportService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {
    @Override
    public List<Report> getAll() {
        return List.of();
    }

    @Override
    public Report getById(UUID id) {
        return null;
    }

    @Override
    public Report create(Report report) {
        return null;
    }

    @Override
    public List<Report> getByAccount(UUID accountId) {
        return List.of();
    }

    @Override
    public Report update(UUID id, Report report) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
