package api.dtos;

import java.math.BigDecimal;

public class CryptoWalletDto {

    private String email;

    private BigDecimal btc;
    private BigDecimal eth;
    private BigDecimal sol;

    public CryptoWalletDto() {
    }

    public CryptoWalletDto(String email) {
        this.email = email;
    }

    public CryptoWalletDto(String email, BigDecimal btc, BigDecimal eth, BigDecimal sol) {
        this.email = email;
        this.btc = btc;
        this.eth = eth;
        this.sol = sol;
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

    @Override
    public String toString() {
        return "CryptoWalletDto{" +
                "email='" + email + '\'' +
                ", btc=" + btc +
                ", eth=" + eth +
                ", sol=" + sol +
                '}';
    }
}