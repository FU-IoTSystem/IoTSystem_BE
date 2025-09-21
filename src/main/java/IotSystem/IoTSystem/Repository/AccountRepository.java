package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);


    @Override
    Optional<Account> findById(UUID uuid);

    // Kiểm tra username đã tồn tại chưa (thường dùng khi register)


}
