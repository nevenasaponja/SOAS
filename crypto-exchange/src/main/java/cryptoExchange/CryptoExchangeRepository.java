package cryptoExchange;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CryptoExchangeRepository extends JpaRepository<CryptoExchangeModel, Integer> {

    CryptoExchangeModel findByFromAndTo(String from, String to);

    @Query(value = """
            SELECT DISTINCT currency_from AS currency FROM crypto_exchange
            UNION
            SELECT DISTINCT currency_to AS currency FROM crypto_exchange
            """, nativeQuery = true)
    List<String> findAllDistinctCurrencis();
}