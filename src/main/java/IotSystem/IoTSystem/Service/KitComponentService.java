package IotSystem.IoTSystem.Service;



import IotSystem.IoTSystem.DTOs.KitComponentRequest;
import IotSystem.IoTSystem.DTOs.Response.KitComponentResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface KitComponentService {

    void createKitComponent(@Valid KitComponentRequest kitComponentRequest);

    Object getKitComponentId(Long id);

    List<KitComponentResponse> getAllKitComponents();

    void deleteKitComponent(Long id, @Valid KitComponentRequest kitComponentRequest);

    void updateKitComponent(Long id, @Valid KitComponentRequest kitComponentRequest);
}
