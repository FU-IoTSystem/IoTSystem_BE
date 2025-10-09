package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Request.*;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface IKitsService {
    // Tạo mới một Kit kèm các component
    KitResponse createKitWithComponents(KitCreationRequest request);

    // Lấy thông tin chi tiết của một Kit (bao gồm các component)
    KitResponse getKitById(UUID kitId);

    // Lấy danh sách tất cả các Kit (có thể kèm component hoặc không)
    List<KitResponse> getAllKits();

    // Cập nhật thông tin Kit và các component
    KitResponse updateKit(UUID kitId, KitRequest request);

    // Xóa một Kit
    void deleteKit(UUID kitId);

    // Lấy riêng danh sách component của một Kit
    List<KitComponentResponse> getComponentsByKitId(UUID kitId);

    KitResponse createSingleKit(KitSingleCreateRequest request);


    KitComponentResponse addSingleComponentToKit(AddSingleComponentRequest request);
    List<KitComponentResponse> addMultipleComponentsToKit(AddMultipleComponentsRequest request);

}
