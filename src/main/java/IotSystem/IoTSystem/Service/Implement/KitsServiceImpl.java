package IotSystem.IoTSystem.Service.Implement;


import IotSystem.IoTSystem.Model.Request.KitRequest;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import IotSystem.IoTSystem.Service.KitsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KitsServiceImpl implements KitsService {


    @Override
    public void createKit(KitRequest kitRequest) {

    }

    @Override
    public Object getKitId(Long id) {
        return null;
    }

    @Override
    public List<KitResponse> getAllKits() {
        return List.of();
    }

    @Override
    public void deleteKit(Long id, KitRequest kitRequest) {

    }

    @Override
    public void updateKit(Long id, KitRequest kitRequest) {

    }
}
