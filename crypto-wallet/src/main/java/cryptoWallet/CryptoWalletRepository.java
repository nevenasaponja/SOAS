package cryptoWallet;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoWalletRepository extends JpaRepository<CryptoWalletModel, Long> {

    CryptoWalletModel findByEmail(String email);

    void deleteByEmail(String email);
}