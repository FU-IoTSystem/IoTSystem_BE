package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Entities.Report;
import IotSystem.IoTSystem.Repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service

public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public List<Report> getAll() {
        return reportRepository.findAll();
    }

    public Report getById(UUID id) {
        return reportRepository.findById(id).orElse(null);
    }

    public List<Report> getByAccount(UUID accountId) {
        return reportRepository.findByGeneratedById(accountId);
    }

    public Report create(Report report) {
        report.setCreatedAt(new Date());
        report.setUpdatedAt(new Date());
        return reportRepository.save(report);
    }

    public Report update(UUID id, Report updated) {
        Report existing = reportRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setTitle(updated.getTitle());
            existing.setStatus(updated.getStatus());
            existing.setUpdatedAt(new Date());
            existing.setGeneratedBy(updated.getGeneratedBy());
            return reportRepository.save(existing);
        }
        return null;
    }

    public void delete(UUID id) {
        reportRepository.deleteById(id);
    }
}
