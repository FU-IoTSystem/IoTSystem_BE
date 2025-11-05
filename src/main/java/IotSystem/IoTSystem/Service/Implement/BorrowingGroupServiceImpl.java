package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles;
import IotSystem.IoTSystem.Model.Entities.StudentGroup;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.BorrowingGroupRepository;
import IotSystem.IoTSystem.Repository.StudentGroupRepository;
import IotSystem.IoTSystem.Service.IBorrowingGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BorrowingGroupServiceImpl implements IBorrowingGroupService {
}