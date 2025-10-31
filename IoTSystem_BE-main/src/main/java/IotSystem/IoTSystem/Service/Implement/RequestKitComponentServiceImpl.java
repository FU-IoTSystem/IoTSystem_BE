package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.RequestKitComponent;
import IotSystem.IoTSystem.Model.Request.RequestKitComponentRequest;
import IotSystem.IoTSystem.Model.Response.RequestKitComponentResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.KitComponentRepository;
import IotSystem.IoTSystem.Repository.RequestKitComponentRepository;
import IotSystem.IoTSystem.Service.IRequestKitComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RequestKitComponentServiceImpl implements IRequestKitComponentService {
    
    @Autowired
    private RequestKitComponentRepository requestKitComponentRepository;
    
    @Autowired
    private KitComponentRepository kitComponentRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Override
    public List<RequestKitComponent> getAll() {
        return requestKitComponentRepository.findAll();
    }
    
    @Override
    public RequestKitComponentResponse getById(UUID id) {
        RequestKitComponent entity = requestKitComponentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request kit component not found"));
        return toResponse(entity);
    }
    
    @Override
    public List<RequestKitComponentResponse> getByRequestId(UUID requestId) {
        List<RequestKitComponent> components = requestKitComponentRepository.findComponentsByRequestId(requestId);
        return components.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public RequestKitComponentResponse create(RequestKitComponentRequest request) {
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify kit component exists
        Kit_Component kitComponent = kitComponentRepository.findById(request.getKitComponentsId())
                .orElseThrow(() -> new RuntimeException("Kit component not found"));
        
        // Check availability
        if (request.getQuantity() > kitComponent.getQuantityAvailable()) {
            throw new RuntimeException("Not enough quantity available");
        }
        
        // Create entity
        RequestKitComponent entity = new RequestKitComponent();
        entity.setRequestId(request.getRequestId());
        entity.setKitComponentsId(request.getKitComponentsId());
        entity.setQuantity(request.getQuantity());
        entity.setComponentName(request.getComponentName());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        entity = requestKitComponentRepository.save(entity);
        
        return toResponse(entity);
    }
    
    @Override
    @Transactional
    public List<RequestKitComponentResponse> createMultiple(List<RequestKitComponentRequest> requests) {
        return requests.stream()
                .map(this::create)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public RequestKitComponentResponse update(UUID id, RequestKitComponentRequest request) {
        RequestKitComponent entity = requestKitComponentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request kit component not found"));
        
        entity.setQuantity(request.getQuantity());
        entity.setUpdatedAt(LocalDateTime.now());
        
        entity = requestKitComponentRepository.save(entity);
        
        return toResponse(entity);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        requestKitComponentRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void deleteByRequestId(UUID requestId) {
        List<RequestKitComponent> components = requestKitComponentRepository.findByRequestId(requestId);
        requestKitComponentRepository.deleteAll(components);
    }
    
    private RequestKitComponentResponse toResponse(RequestKitComponent entity) {
        RequestKitComponentResponse response = new RequestKitComponentResponse();
        response.setId(entity.getId());
        response.setRequestId(entity.getRequestId());
        response.setKitComponentsId(entity.getKitComponentsId());
        response.setQuantity(entity.getQuantity());
        response.setComponentName(entity.getComponentName());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        
        // Load additional component details if available
        if (entity.getKitComponent() != null) {
            response.setComponentType(entity.getKitComponent().getComponentType() != null ? 
                    entity.getKitComponent().getComponentType().name() : null);
            response.setDescription(entity.getKitComponent().getDescription());
        }
        
        return response;
    }
}

