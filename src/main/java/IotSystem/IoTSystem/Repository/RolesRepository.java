package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository

public interface RolesRepository extends JpaRepository<Roles, Long> {


    //  Tìm role theo tên (phục vụ đăng ký gán mặc định role USER hoặc tìm role ADMIN)
    Optional<Roles> findByName(String name);

    // Kiểm tra role đã tồn tại chưa (phục vụ seed dữ liệu ban đầu)
    boolean existsByName(String roleName);

}
