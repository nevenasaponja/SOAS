package cryptoWallet;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class CryptoWalletModel {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(precision = 19, scale = 8)
    private BigDecimal btc = BigDecimal.ZERO;

    @Column(precision = 19, scale = 8)
    private BigDecimal eth = BigDecimal.ZERO;

    @Column(precision = 19, scale = 8)
    private BigDecimal sol = BigDecimal.ZERO;

    public CryptoWalletModel() {
    }

    public CryptoWalletModel(String email) {
        this.email = email;
        this.btc = BigDecimal.ZERO;
        this.eth = BigDecimal.ZERO;
        this.sol = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getBtc() {
        return btc;
    }

    public BigDecimal getEth() {
        return eth;
    }

    public BigDecimal getSol() {
        return sol;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBtc(BigDecimal btc) {
        this.btc = btc;
    }

    public void setEth(BigDecimal eth) {
        this.eth = eth;
    }

    public void setSol(BigDecimal sol) {
        this.sol = sol;
    }
}