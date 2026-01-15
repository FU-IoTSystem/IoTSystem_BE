package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Request.KitComponentHistoryRequest;
import IotSystem.IoTSystem.Model.Response.KitComponentHistoryResponse;

import java.util.List;
import java.util.UUID;

public interface IKitComponentHistoryService {
    KitComponentHistoryResponse create(KitComponentHistoryRequest request);
    List<KitComponentHistoryResponse> getAll();
    List<KitComponentHistoryResponse> getByKitId(UUID kitId);
    List<KitComponentHistoryResponse> getByComponentId(UUID componentId);
    List<KitComponentHistoryResponse> getGlobalComponentsHistory();
}

