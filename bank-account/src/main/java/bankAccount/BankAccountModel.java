package bankAccount;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BankAccountModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private BigDecimal eur;
    private BigDecimal usd;
    private BigDecimal gbp;
    private BigDecimal chf;
    private BigDecimal rsd;

    public BankAccountModel() {
    }

    public BankAccountModel(String email, BigDecimal eur, BigDecimal usd,
                            BigDecimal gbp, BigDecimal chf, BigDecimal rsd) {
        this.email = email;
        this.eur = eur;
        this.usd = usd;
        this.gbp = gbp;
        this.chf = chf;
        this.rsd = rsd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}