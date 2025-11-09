package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Mappers.ClassResponseMapper;
import IotSystem.IoTSystem.Model.Request.ClassRequest;
import IotSystem.IoTSystem.Model.Response.ClassResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.ClassesRepository;
import IotSystem.IoTSystem.Service.IClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClassesServiceImpl  implements IClassesService {



    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<ClassResponse> getAll() {
        List<Classes> classes = classesRepository.findAll();
        return classes.stream().map(clas ->
                ClassResponseMapper.toResponse(clas, clas.getAccount().getId())).toList();
    }

    @Override
    public Classes getById(UUID id) {
        return null;
    }

    @Override
    public ClassResponse create(ClassRequest request, UUID teacherID) {
        Classes classes = new Classes();

        classes.setClassCode(request.getClassCode());
        classes.setSemester(request.getSemester());
        classes.setStatus(request.isStatus());

        Account account = accountRepository.findById(teacherID)
                .orElseThrow(() -> new ResourceNotFoundException("Did not found the Lecturer: " + teacherID));
        classes.setAccount(account);

        classesRepository.save(classes);

        return ClassResponseMapper.toResponse(classes, classes.getAccount().getId());
    }

    @Override
    public ClassResponse update(UUID id, ClassRequest request) {
        Classes editing = classesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Did not found the Class: " + id));
        Account account = accountRepository.findById(request.getTeacherId()).orElseThrow(() -> new ResourceNotFoundException("Did not found the Lecturer: " + request.getTeacherId()));

        editing.setAccount(account);
        editing.setSemester(request.getSemester());
        editing.setStatus(request.isStatus());
        editing.setClassCode(request.getClassCode());

        classesRepository.save(editing);
        return ClassResponseMapper.toResponse(editing, editing.getAccount().getId());
    }

    @Override
    public void delete(UUID id) {

    }
}
