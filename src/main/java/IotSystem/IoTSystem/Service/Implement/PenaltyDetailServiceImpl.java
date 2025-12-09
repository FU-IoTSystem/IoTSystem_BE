package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.Penalty;
import IotSystem.IoTSystem.Model.Entities.PenaltyDetail;
import IotSystem.IoTSystem.Model.Entities.PenaltyPolicies;
import IotSystem.IoTSystem.Model.Request.PenaltyDetailRequest;
import IotSystem.IoTSystem.Model.Response.PenaltyDetailResponse;
import IotSystem.IoTSystem.Repository.PenaltyDetailRepository;
import IotSystem.IoTSystem.Repository.PenaltyPoliciesRepository;
import IotSystem.IoTSystem.Repository.PenaltyRepository;
import IotSystem.IoTSystem.Service.IPenaltyDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PenaltyDetailServiceImpl implements IPenaltyDetailService {

    @Autowired
    private PenaltyDetailRepository penaltyDetailRepository;

    @Autowired
    private PenaltyPoliciesRepository penaltyPoliciesRepository;

    @Autowired
    private PenaltyRepository penaltyRepository;

    @Override
    public List<PenaltyDetail> getAll() {
        return penaltyDetailRepository.findAll();
    }

    @Override
    public PenaltyDetail getById(UUID id) {
        return penaltyDetailRepository.findById(id).orElse(null);
    }

    @Override
    public PenaltyDetail create(PenaltyDetailRequest request) {
        PenaltyDetail detail = new PenaltyDetail();
        detail.setAmount(request.getAmount());
        detail.setDescription(request.getDescription());

        // Set createdAt - use request value or current date
        if (request.getCreatedAt() != null) {
            detail.setCreatedAt(request.getCreatedAt());
        } else {
            detail.setCreatedAt(LocalDateTime.now());
        }

        // Set relationships
        if (request.getPoliciesId() != null) {
            PenaltyPolicies policies = penaltyPoliciesRepository.findById(request.getPoliciesId()).orElse(null);
            detail.setPolicies(policies);
        }

        if (request.getPenaltyId() != null) {
            Penalty penalty = penaltyRepository.findById(request.getPenaltyId()).orElse(null);
            detail.setPenalty(penalty);
        }

        return penaltyDetailRepository.save(detail);
    }

    @Override
    public List<PenaltyDetail> createMultiple(List<PenaltyDetailRequest> requests) {
        List<PenaltyDetail> details = new ArrayList<>();
        for (PenaltyDetailRequest request : requests) {
            details.add(create(request));
        }
        return details;
    }

    @Override
    public PenaltyDetail update(UUID id, PenaltyDetailRequest request) {
        PenaltyDetail detail = getById(id);
        if (detail == null) {
            return null;
        }

        if (request.getAmount() != null) {
            detail.setAmount(request.getAmount());
        }

        if (request.getDescription() != null) {
            detail.setDescription(request.getDescription());
        }

        // Update relationships if needed
        if (request.getPoliciesId() != null) {
            PenaltyPolicies policies = penaltyPoliciesRepository.findById(request.getPoliciesId()).orElse(null);
            detail.setPolicies(policies);
        }

        if (request.getPenaltyId() != null) {
            Penalty penalty = penaltyRepository.findById(request.getPenaltyId()).orElse(null);
            detail.setPenalty(penalty);
        }

        return penaltyDetailRepository.save(detail);
    }

    @Override
    public void delete(UUID id) {
        penaltyDetailRepository.deleteById(id);
    }

    @Override
    public List<PenaltyDetailResponse> findByPenaltyId(UUID penaltyId) {

        List<PenaltyDetail> penaltyDetails = penaltyDetailRepository.findByPenaltyId(penaltyId);

        return penaltyDetails.stream().map(PenaltyDetailMapper::toResponse).toList();
    }

    @Override
    public List<PenaltyDetail> findByPoliciesId(UUID policiesId) {
        return penaltyDetailRepository.findByPoliciesId(policiesId);
    }
}

