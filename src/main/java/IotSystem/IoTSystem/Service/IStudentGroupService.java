package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Model.Entities.StudentGroup;

import java.util.List;
import java.util.UUID;

public interface IStudentGroupService {
    List<StudentGroup> getAll();

    StudentGroup getById(UUID id);

    StudentGroup create(StudentGroup group);

    StudentGroup update(UUID id, StudentGroup group);

    void delete(UUID id);
}
