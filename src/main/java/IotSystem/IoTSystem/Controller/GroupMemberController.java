package IotSystem.IoTSystem.Controller;

import IotSystem.IoTSystem.Entities.GroupMember;
import IotSystem.IoTSystem.Service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/group-members")
public class GroupMemberController {


    @Autowired
    private GroupMemberService groupMemberService;

    @GetMapping("/GETALL")
    public List<GroupMember> getAll() {
        return groupMemberService.getAll();
    }

    @GetMapping("/getById/{id}")
    public GroupMember getById(@PathVariable UUID id) {
        return groupMemberService.getById(id);
    }

    @PostMapping("POst")
    public GroupMember create(@RequestBody GroupMember member) {
        return groupMemberService.create(member);
    }

    @PutMapping("/update/{id}")
    public GroupMember update(@PathVariable UUID id, @RequestBody GroupMember member) {
        return groupMemberService.update(id, member);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        groupMemberService.delete(id);
    }
}
