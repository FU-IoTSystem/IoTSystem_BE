package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByStudentCode(String studentCode);

    // Check if email exists for another account (excluding current account)
    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.email = :email AND a.id <> :excludeId")
    boolean existsByEmailExcludingId(@Param("email") String email, @Param("excludeId") UUID excludeId);

    // Check if studentCode exists for another account (excluding current account)
    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.studentCode = :studentCode AND a.id <> :excludeId AND a.studentCode IS NOT NULL AND a.studentCode <> ''")
    boolean existsByStudentCodeExcludingId(@Param("studentCode") String studentCode, @Param("excludeId") UUID excludeId);

    // Check if phone exists for another account (excluding current account)
    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.phone = :phone AND a.id <> :excludeId AND a.phone IS NOT NULL AND a.phone <> ''")
    boolean existsByPhoneExcludingId(@Param("phone") String phone, @Param("excludeId") UUID excludeId);

    @Query("SELECT a FROM Account a WHERE a.role.name <> 'ADMIN'")
    List<Account> findAllExceptAdmin();

    @Override
    Optional<Account> findById(UUID uuid);

    List<Account> findByRole(Roles role);

    // Kiểm tra username đã tồn tại chưa (thường dùng khi register)

    @Query("SELECT a FROM Account a WHERE a.role.name = 'LECTURER' AND a.isActive = true")
    List<Account> findAllLecturers();


}
