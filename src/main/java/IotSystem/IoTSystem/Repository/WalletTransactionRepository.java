package IotSystem.IoTSystem.Repository;

import IotSystem.IoTSystem.Model.Entities.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {

    List<WalletTransaction> findAll();

    WalletTransaction getById(UUID id);

    WalletTransaction save(WalletTransaction transaction);

    void deleteById(UUID id);
    
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.wallet.id = :walletId ORDER BY wt.createdAt DESC")
    List<WalletTransaction> findTransactionsByWalletId(@Param("walletId") UUID walletId);

    @Query("SELECT t FROM WalletTransaction t WHERE t.transactionType <> 'TOP_UP' ORDER BY t.createdAt DESC")
    List<WalletTransaction> findAllExceptTopUp();
}
