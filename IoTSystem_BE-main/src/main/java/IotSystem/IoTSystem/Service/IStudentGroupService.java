package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.StudentGroup;
import IotSystem.IoTSystem.Model.Request.StudentGroupRequest;
import IotSystem.IoTSystem.Model.Response.StudentGroupResponse;

import java.util.List;
import java.util.UUID;

public interface IStudentGroupService {
    List<StudentGroupResponse> getAll();

    StudentGroupResponse getById(UUID id);

    StudentGroupResponse create(StudentGroupRequest request);

    StudentGroup update(UUID id, StudentGroup group);

    void delete(UUID id);
}
