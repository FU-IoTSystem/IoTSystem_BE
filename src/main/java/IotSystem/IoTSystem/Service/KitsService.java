package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Request.KitRequest;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface KitsService {
    void createKit(@Valid KitRequest kitRequest);

    Object getKitId(Long id);

    List<KitResponse> getAllKits();

    void deleteKit(Long id, @Valid KitRequest kitRequest);

    void updateKit(Long id, @Valid KitRequest kitRequest);
}
