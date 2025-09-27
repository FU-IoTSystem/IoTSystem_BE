package IotSystem.IoTSystem.Model.Response;


import IotSystem.IoTSystem.Model.Entities.Enum.Status.ReportStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DamageReportResponse {
    private UUID id;
    private String description;
    private ReportStatus status;


    private String generatedByEmail;
}
