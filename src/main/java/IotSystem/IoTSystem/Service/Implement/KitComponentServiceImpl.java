package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Request.KitComponentRequest;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
import IotSystem.IoTSystem.Service.IKitComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KitComponentServiceImpl implements IKitComponentService {

    @Override
    public void createKitComponent(KitComponentRequest kitComponentRequest) {

    }

    @Override
    public Object getKitComponentId(Long id) {
        return null;
    }

    @Override
    public List<KitComponentResponse> getAllKitComponents() {
        return List.of();
    }

    @Override
    public void deleteKitComponent(Long id, KitComponentRequest kitComponentRequest) {

    }

    @Override
    public void updateKitComponent(Long id, KitComponentRequest kitComponentRequest) {

    }
}
