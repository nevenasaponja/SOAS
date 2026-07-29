package tradeService;

import jakarta.persistence.*;

@Entity
@Table(name = "trade_model")
public class TradeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String crypto;
    private String fiat;
    private double rate;

    public TradeModel() {
    }

    public TradeModel(String crypto, String fiat, double rate) {
        this.crypto = crypto;
        this.fiat = fiat;
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public String getCrypto() {
        return crypto;
    }

    public void setCrypto(String crypto) {
        this.crypto = crypto;
    }

    public String getFiat() {
        return fiat;
    }

    public void setFiat(String fiat) {
        this.fiat = fiat;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}