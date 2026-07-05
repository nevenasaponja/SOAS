package api.dtos;

import java.math.BigDecimal;

public class BankAccountDto {

    private String email;

    private BigDecimal eur;
    private BigDecimal usd;
    private BigDecimal gbp;
    private BigDecimal chf;
    private BigDecimal rsd;

    public BankAccountDto() {
    }

    public BankAccountDto(String email) {
        this.email = email;
    }

    public BankAccountDto(String email, BigDecimal eur, BigDecimal usd, BigDecimal gbp, BigDecimal chf, BigDecimal rsd) {
        this.email = email;
        this.eur = eur;
        this.usd = usd;
        this.gbp = gbp;
        this.chf = chf;
        this.rsd = rsd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public BigDecimal getEur() {
        return eur;
    }

    public void setEur(BigDecimal eur) {
        this.eur = eur;
    }

    public BigDecimal getUsd() {
        return usd;
    }

    public void setUsd(BigDecimal usd) {
        this.usd = usd;
    }

    public BigDecimal getGbp() {
        return gbp;
    }

    public void setGbp(BigDecimal gbp) {
        this.gbp = gbp;
    }

    public BigDecimal getChf() {
        return chf;
    }

    public void setChf(BigDecimal chf) {
        this.chf = chf;
    }

    public BigDecimal getRsd() {
        return rsd;
    }

    public void setRsd(BigDecimal rsd) {
        this.rsd = rsd;
    }
    @Override
    public String toString() {
        return "BankAccountDto{" +
                "email='" + email + '\'' +
                ", eur=" + eur +
                ", usd=" + usd +
                ", gbp=" + gbp +
                ", chf=" + chf +
                ", rsd=" + rsd +
                '}';
    }
}