package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.KitComponentHistory;
import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Entities.PenaltyDetail;
import IotSystem.IoTSystem.Model.Mappers.KitComponentHistoryMapper;
import IotSystem.IoTSystem.Model.Request.KitComponentHistoryRequest;
import IotSystem.IoTSystem.Model.Response.KitComponentHistoryResponse;
import IotSystem.IoTSystem.Repository.KitComponentHistoryRepository;
import IotSystem.IoTSystem.Repository.KitComponentRepository;
import IotSystem.IoTSystem.Repository.KitsRepository;
import IotSystem.IoTSystem.Repository.PenaltyDetailRepository;
import IotSystem.IoTSystem.Service.IKitComponentHistoryService;
import IotSystem.IoTSystem.Service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KitComponentHistoryServiceImpl implements IKitComponentHistoryService {

    private final KitComponentHistoryRepository historyRepository;
    private final KitsRepository kitsRepository;
    private final KitComponentRepository kitComponentRepository;
    private final PenaltyDetailRepository penaltyDetailRepository;
    private final WebSocketService webSocketService;

    @Override
    @Transactional
    public KitComponentHistoryResponse create(KitComponentHistoryRequest request) {
        Kit_Component component = kitComponentRepository.findById(request.getComponentId())
                .orElseThrow(() -> new ResourceNotFoundException("Component not found with ID: " + request.getComponentId()));

        Kits kit = null;
        if (request.getKitId() != null) {
            kit = kitsRepository.findById(request.getKitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kit not found with ID: " + request.getKitId()));
        } else {
            // If kitId is not provided (e.g. from virtual kit context), try to get from component
            if (component.getKit() != null) {
                kit = component.getKit();
            }
        }

        PenaltyDetail penaltyDetail = null;
        if (request.getPenaltyDetailId() != null) {
            penaltyDetail = penaltyDetailRepository.findById(request.getPenaltyDetailId())
                    .orElseThrow(() -> new ResourceNotFoundException("Penalty detail not found with ID: " + request.getPenaltyDetailId()));
        }

        KitComponentHistory history = KitComponentHistory.builder()
                .kit(kit)
                .component(component)
                .action(request.getAction())
                .oldStatus(request.getOldStatus())
                .newStatus(request.getNewStatus())
                .imgUrl(request.getImgUrl())

                .penaltyDetail(penaltyDetail)
                .build();

        history = historyRepository.save(history);

        webSocketService.sendSystemUpdate("HISTORY", "CREATE");

        return KitComponentHistoryMapper.toResponse(history);
    }

    @Override
    public List<KitComponentHistoryResponse> getAll() {
        return historyRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(KitComponentHistoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<KitComponentHistoryResponse> getByKitId(UUID kitId) {
        return historyRepository.findByKitIdOrderByCreatedAtDesc(kitId).stream()
                .map(KitComponentHistoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<KitComponentHistoryResponse> getByComponentId(UUID componentId) {
        return historyRepository.findByComponentIdOrderByCreatedAtDesc(componentId).stream()
                .map(KitComponentHistoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<KitComponentHistoryResponse> getGlobalComponentsHistory() {
        return historyRepository.findByKitIsNullOrderByCreatedAtDesc().stream()
                .map(KitComponentHistoryMapper::toResponse)
                .collect(Collectors.toList());
    }
}

