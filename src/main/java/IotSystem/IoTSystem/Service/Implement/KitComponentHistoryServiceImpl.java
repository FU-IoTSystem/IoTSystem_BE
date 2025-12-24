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

    @Override
    @Transactional
    public KitComponentHistoryResponse create(KitComponentHistoryRequest request) {
        Kits kit = kitsRepository.findById(request.getKitId())
                .orElseThrow(() -> new ResourceNotFoundException("Kit not found with ID: " + request.getKitId()));

        Kit_Component component = kitComponentRepository.findById(request.getComponentId())
                .orElseThrow(() -> new ResourceNotFoundException("Component not found with ID: " + request.getComponentId()));

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
                .note(request.getNote())
                .penaltyDetail(penaltyDetail)
                .build();

        history = historyRepository.save(history);

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
}

