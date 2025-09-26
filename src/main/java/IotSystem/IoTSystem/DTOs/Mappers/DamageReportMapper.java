package IotSystem.IoTSystem.DTOs.Mappers;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.DamageReport;
import IotSystem.IoTSystem.Model.Request.DamageReportRequest;
import IotSystem.IoTSystem.Model.Response.DamageReportResponse;
import org.springframework.stereotype.Component;

@Component
public class DamageReportMapper {
    public DamageReport toEntity(DamageReportRequest request, Account account) {
        DamageReport report = new DamageReport();
        report.setDescription(request.getDescription());
        report.setStatus(request.getStatus());
        report.setGeneratedBy(account);
        return report;
    }

    public DamageReportResponse toResponse(DamageReport report) {
        DamageReportResponse response = new DamageReportResponse();
        response.setId(report.getId());
        response.setDescription(report.getDescription());
        response.setStatus(report.getStatus());
        if (report.getGeneratedBy() != null) {

            response.setGeneratedByEmail(report.getGeneratedBy().getEmail());
        }
        return response;
    }
}
