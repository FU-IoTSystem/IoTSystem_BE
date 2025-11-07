package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Request.ExcelImportRequest;
import IotSystem.IoTSystem.Model.Response.ExcelImportResponse;

public interface IExcelImportService {
    ExcelImportResponse importAccounts(ExcelImportRequest request);
}
