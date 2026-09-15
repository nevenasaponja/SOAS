package bankAccount;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccountModel, Long> {

    BankAccountModel findByEmail(String email);

    void deleteByEmail(String email);
}