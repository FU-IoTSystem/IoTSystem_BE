package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.DamageReport;
import IotSystem.IoTSystem.Service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {


    @Autowired
    private IReportService reportService;

    @GetMapping("/getAll")
    public List<DamageReport> getAll() {
        return reportService.getAll();
    }

    @GetMapping("/getById/{id}")
    public DamageReport getById(@PathVariable UUID id) {
        return reportService.getById(id);
    }

    @GetMapping("/account/{accountId}")
    public List<DamageReport> getByAccount(@PathVariable UUID accountId) {
        return reportService.getByAccount(accountId);
    }

    @PostMapping("/create")
    public DamageReport create(@RequestBody DamageReport report) {
        return reportService.create(report);
    }

    @PutMapping("/update/{id}")
    public DamageReport update(@PathVariable UUID id, @RequestBody DamageReport report) {
        return reportService.update(id, report);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        reportService.delete(id);
    }
}
