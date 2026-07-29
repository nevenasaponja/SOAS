package tradeService;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<TradeModel, Long> {

    TradeModel findByCryptoAndFiat(String crypto, String fiat);

}