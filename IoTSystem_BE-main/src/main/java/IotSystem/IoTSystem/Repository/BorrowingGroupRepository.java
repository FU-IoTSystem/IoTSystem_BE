package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.BorrowingGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BorrowingGroupRepository extends JpaRepository<BorrowingGroup, UUID> {
    
    // Find all borrowing groups by student group
    List<BorrowingGroup> findByStudentGroupId(UUID studentGroupId);
    
    // Find all borrowing groups by account
    List<BorrowingGroup> findByAccountId(UUID accountId);
    
    // Find borrowing group by student group and account
    BorrowingGroup findByStudentGroupIdAndAccountId(UUID studentGroupId, UUID accountId);
    
    // Find all borrowing groups with specific role
    List<BorrowingGroup> findByRoles(IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles roles);
    
    // Find borrowing groups by student group and role
    List<BorrowingGroup> findByStudentGroupIdAndRoles(UUID studentGroupId, IotSystem.IoTSystem.Model.Entities.Enum.GroupRoles roles);
    
    // Check if account exists in student group
    boolean existsByStudentGroupIdAndAccountId(UUID studentGroupId, UUID accountId);
    
    // Count members in a student group
    long countByStudentGroupId(UUID studentGroupId);
    
    // Find all borrowing groups with student group details
    @Query("SELECT bg FROM BorrowingGroup bg JOIN FETCH bg.studentGroup sg JOIN FETCH bg.account a WHERE bg.studentGroup.id = :studentGroupId")
    List<BorrowingGroup> findByStudentGroupIdWithDetails(@Param("studentGroupId") UUID studentGroupId);
}
