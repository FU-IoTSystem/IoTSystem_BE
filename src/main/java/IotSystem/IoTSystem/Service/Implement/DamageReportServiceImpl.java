package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.*;
import IotSystem.IoTSystem.Model.Request.DamageReportRequest;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.BorrowingRequestRepository;
import IotSystem.IoTSystem.Repository.DamageReportRepository;
import IotSystem.IoTSystem.Repository.KitsRepository;
import IotSystem.IoTSystem.Service.IDamageReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DamageReportServiceImpl implements IDamageReportService {

    @Autowired
    private DamageReportRepository damageReportRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KitsRepository kitsRepository;

    @Autowired
    private BorrowingRequestRepository borrowingRequestRepository;

    @Override
    public List<DamageReport> getAll() {
        return damageReportRepository.findAll();
    }

    @Override
    public DamageReport getById(UUID id) {
        return damageReportRepository.findById(id).orElse(null);
    }

    @Override
    public DamageReport create(DamageReportRequest request) {
        DamageReport report = new DamageReport();
        report.setDescription(request.getDescription());
        report.setStatus(request.getStatus());
        report.setTotalDamageValue(request.getTotalDamageValue());

        // Set relationships
        if (request.getGeneratedByEmail() != null) {
            Account account = accountRepository.findByEmail(request.getGeneratedByEmail()).orElse(null);
            report.setGeneratedBy(account);
        }

        if (request.getKitId() != null) {
            Kits kit = kitsRepository.findById(request.getKitId()).orElse(null);
            report.setKit(kit);
        }

        if (request.getBorrowRequestId() != null) {
            BorrowingRequest borrowRequest = borrowingRequestRepository.findById(request.getBorrowRequestId()).orElse(null);
            report.setBorrowRequest(borrowRequest);
        }

        return damageReportRepository.save(report);
    }

    @Override
    public DamageReport update(UUID id, DamageReportRequest request) {
        DamageReport report = getById(id);
        if (report == null) {
            return null;
        }

        if (request.getDescription() != null) {
            report.setDescription(request.getDescription());
        }

        if (request.getStatus() != null) {
            report.setStatus(request.getStatus());
        }

        if (request.getTotalDamageValue() != null) {
            report.setTotalDamageValue(request.getTotalDamageValue());
        }

        // Update relationships if provided
        if (request.getGeneratedByEmail() != null) {
            Account account = accountRepository.findByEmail(request.getGeneratedByEmail()).orElse(null);
            report.setGeneratedBy(account);
        }

        if (request.getKitId() != null) {
            Kits kit = kitsRepository.findById(request.getKitId()).orElse(null);
            report.setKit(kit);
        }

        if (request.getBorrowRequestId() != null) {
            BorrowingRequest borrowRequest = borrowingRequestRepository.findById(request.getBorrowRequestId()).orElse(null);
            report.setBorrowRequest(borrowRequest);
        }

        return damageReportRepository.save(report);
    }

    @Override
    public void delete(UUID id) {
        damageReportRepository.deleteById(id);
    }

    @Override
    public List<DamageReport> getByAccountId(UUID accountId) {
        return damageReportRepository.findAll().stream()
                .filter(report -> report.getGeneratedBy() != null && report.getGeneratedBy().getId().equals(accountId))
                .toList();
    }

    @Override
    public List<DamageReport> getByKitId(UUID kitId) {
        return damageReportRepository.findAll().stream()
                .filter(report -> report.getKit() != null && report.getKit().getId().equals(kitId))
                .toList();
    }

    @Override
    public List<DamageReport> getByBorrowRequestId(UUID borrowRequestId) {
        return damageReportRepository.findAll().stream()
                .filter(report -> report.getBorrowRequest() != null && report.getBorrowRequest().getId().equals(borrowRequestId))
                .toList();
    }

    @Override
    public List<DamageReport> getByStatus(String status) {
        return damageReportRepository.findAll().stream()
                .filter(report -> report.getStatus() != null && report.getStatus().toString().equals(status))
                .toList();
    }
}

