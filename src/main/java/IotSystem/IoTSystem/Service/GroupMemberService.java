package IotSystem.IoTSystem.Service;

import IotSystem.IoTSystem.Entities.GroupMember;
import IotSystem.IoTSystem.Repository.GroupMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service

public class GroupMemberService {


    @Autowired
    private GroupMemberRepository groupMemberRepository;

    public List<GroupMember> getAll() {
        return groupMemberRepository.findAll();
    }

    public GroupMember getById(UUID id) {
        return groupMemberRepository.findById(id).orElse(null);
    }

    public GroupMember create(GroupMember member) {
        return groupMemberRepository.save(member);
    }

    public GroupMember update(UUID id, GroupMember updated) {
        GroupMember existing = groupMemberRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setRole(updated.getRole());
            existing.setGroup(updated.getGroup());
            existing.setUser(updated.getUser());
            return groupMemberRepository.save(existing);
        }
        return null;
    }

    public void delete(UUID id) {
        groupMemberRepository.deleteById(id);
    }
}
