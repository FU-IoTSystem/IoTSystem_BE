package IotSystem.IoTSystem.Service;



import IotSystem.IoTSystem.Model.Request.KitComponentRequest;
import IotSystem.IoTSystem.Model.Response.ExcelImportResponse;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface IKitComponentService {

    KitComponentResponse createKitComponent(@Valid KitComponentRequest kitComponentRequest);

    Object getKitComponentId(Long id);

    List<KitComponentResponse> getAllKitComponents();

    List<KitComponentResponse> getKitComponentsWithoutKit();


    KitResponse deleteKitComponent(UUID id);

    KitComponentResponse updateKitComponent(UUID id, @Valid KitComponentRequest kitComponentRequest);

    ExcelImportResponse importComponentsFromExcel(UUID kitId, String fileContent, String fileName);

    ExcelImportResponse importComponentsFromExcel(UUID kitId, String fileContent, String fileName, String sheetName);
}
