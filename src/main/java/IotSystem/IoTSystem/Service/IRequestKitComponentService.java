package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.RequestKitComponent;
import IotSystem.IoTSystem.Model.Request.RequestKitComponentRequest;
import IotSystem.IoTSystem.Model.Response.RequestKitComponentResponse;

import java.util.List;
import java.util.UUID;

public interface IRequestKitComponentService {
    List<RequestKitComponent> getAll();

    RequestKitComponentResponse getById(UUID id);

    List<RequestKitComponentResponse> getByRequestId(UUID requestId);

    RequestKitComponentResponse create(RequestKitComponentRequest request);

    List<RequestKitComponentResponse> createMultiple(List<RequestKitComponentRequest> requests);

    RequestKitComponentResponse update(UUID id, RequestKitComponentRequest request);

    void delete(UUID id);

    void deleteByRequestId(UUID requestId);
}

