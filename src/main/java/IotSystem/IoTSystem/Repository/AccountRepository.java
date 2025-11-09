package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);


    @Override
    Optional<Account> findById(UUID uuid);

    List<Account> findByRole(Roles role);

// Kiểm tra username đã tồn tại chưa (thường dùng khi register)


}