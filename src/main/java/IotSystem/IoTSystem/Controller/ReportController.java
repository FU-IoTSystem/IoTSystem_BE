package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Model.Entities.Report;
import IotSystem.IoTSystem.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {


    @Autowired
    private ReportService reportService;

    @GetMapping("/getAll")
    public List<Report> getAll() {
        return reportService.getAll();
    }

    @GetMapping("/getById/{id}")
    public Report getById(@PathVariable UUID id) {
        return reportService.getById(id);
    }

    @GetMapping("/account/{accountId}")
    public List<Report> getByAccount(@PathVariable UUID accountId) {
        return reportService.getByAccount(accountId);
    }

    @PostMapping("/create")
    public Report create(@RequestBody Report report) {
        return reportService.create(report);
    }

    @PutMapping("/update/{id}")
    public Report update(@PathVariable UUID id, @RequestBody Report report) {
        return reportService.update(id, report);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        reportService.delete(id);
    }
}
