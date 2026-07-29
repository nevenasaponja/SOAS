package tradeService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.CryptoWalletDto;
import api.proxies.BankAccountProxy;
import api.proxies.CryptoWalletProxy;
import feign.FeignException;

@RestController
public class TradeServiceImpl {

    @Autowired
    private BankAccountProxy bankAccountProxy;

    @Autowired
    private CryptoWalletProxy cryptoWalletProxy;

    @Autowired
    private TradeRepository tradeRepository;

    @GetMapping("/trade-service")
    public String trade(
            @RequestParam String email,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal quantity) {

        // Provera email adrese
        if (email == null || email.trim().isEmpty()) {
            return "Email ne sme biti prazan.";
        }

        // Provera valuta
        if (from == null || from.trim().isEmpty()
                || to == null || to.trim().isEmpty()) {
            return "Valute 'from' i 'to' moraju biti unete.";
        }

        // Količina mora biti pozitivna
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return "Količina mora biti veća od 0.";
        }

        from = from.trim().toUpperCase();
        to = to.trim().toUpperCase();
        email = email.trim();

        // Nije dozvoljeno trgovanje iste valute
        if (from.equals(to)) {
            return "Početna i ciljna valuta ne mogu biti iste.";
        }

        // Kupovina kriptovalute fiat valutom
        if (isFiat(from) && isCrypto(to)) {
            return buyCrypto(email, from, to, quantity);
        }

        // Prodaja kriptovalute za fiat valutu
        if (isCrypto(from) && isFiat(to)) {
            return sellCrypto(email, from, to, quantity);
        }

        return "Neispravna trgovina. Dozvoljeno je samo FIAT -> CRYPTO ili CRYPTO -> FIAT.";
    }

    private String buyCrypto(
            String email,
            String fiat,
            String crypto,
            BigDecimal fiatAmount) {

        BankAccountDto bankAccount;
        CryptoWalletDto cryptoWallet;

        try {
            bankAccount = bankAccountProxy.getAccountByEmail(email);
        } catch (FeignException.NotFound exception) {
            return "Bankovni račun ne postoji za email: " + email;
        } catch (FeignException exception) {
            return "Greška prilikom pristupa Bank Account servisu.";
        }

        try {
            cryptoWallet = cryptoWalletProxy.getWalletByEmail(email);
        } catch (FeignException.NotFound exception) {
            return "Crypto wallet ne postoji za email: " + email;
        } catch (FeignException exception) {
            return "Greška prilikom pristupa Crypto Wallet servisu.";
        }

        if (bankAccount == null) {
            return "Bankovni račun ne postoji za email: " + email;
        }

        if (cryptoWallet == null) {
            return "Crypto wallet ne postoji za email: " + email;
        }

        BigDecimal rate = getTradeRate(crypto, fiat);

        if (rate == null) {
            return "Ne postoji kurs za " + crypto + "/" + fiat + ".";
        }

        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            return "Kurs za " + crypto + "/" + fiat + " nije ispravan.";
        }

        BigDecimal fiatBalance = getFiatBalance(bankAccount, fiat);

        if (fiatBalance.compareTo(fiatAmount) < 0) {
            return "Nemate dovoljno sredstava u valuti " + fiat
                    + ". Trenutno stanje: " + fiatBalance + " " + fiat + ".";
        }

        BigDecimal cryptoAmount = fiatAmount.divide(
                rate,
                8,
                RoundingMode.HALF_UP
        );

        subtractFiat(bankAccount, fiat, fiatAmount);
        addCrypto(cryptoWallet, crypto, cryptoAmount);

        try {
            bankAccountProxy.updateAccount(bankAccount);
            cryptoWalletProxy.updateWallet(cryptoWallet);
        } catch (FeignException exception) {
            return "Došlo je do greške prilikom ažuriranja računa ili crypto wallet-a.";
        }

        return "Uspešno je izvršena kupovina "
                + cryptoAmount + " " + crypto
                + " za " + fiatAmount + " " + fiat
                + ". Novo stanje računa: " + bankAccount
                + ". Novo stanje crypto wallet-a: " + cryptoWallet;
    }

    private String sellCrypto(
            String email,
            String crypto,
            String fiat,
            BigDecimal cryptoAmount) {

        BankAccountDto bankAccount;
        CryptoWalletDto cryptoWallet;

        try {
            bankAccount = bankAccountProxy.getAccountByEmail(email);
        } catch (FeignException.NotFound exception) {
            return "Bankovni račun ne postoji za email: " + email;
        } catch (FeignException exception) {
            return "Greška prilikom pristupa Bank Account servisu.";
        }

        try {
            cryptoWallet = cryptoWalletProxy.getWalletByEmail(email);
        } catch (FeignException.NotFound exception) {
            return "Crypto wallet ne postoji za email: " + email;
        } catch (FeignException exception) {
            return "Greška prilikom pristupa Crypto Wallet servisu.";
        }

        if (bankAccount == null) {
            return "Bankovni račun ne postoji za email: " + email;
        }

        if (cryptoWallet == null) {
            return "Crypto wallet ne postoji za email: " + email;
        }

        BigDecimal rate = getTradeRate(crypto, fiat);

        if (rate == null) {
            return "Ne postoji kurs za " + crypto + "/" + fiat + ".";
        }

        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            return "Kurs za " + crypto + "/" + fiat + " nije ispravan.";
        }

        BigDecimal cryptoBalance = getCryptoBalance(cryptoWallet, crypto);

        if (cryptoBalance.compareTo(cryptoAmount) < 0) {
            return "Nemate dovoljno crypto sredstava u valuti "
                    + crypto + ". Trenutno stanje: "
                    + cryptoBalance + " " + crypto + ".";
        }

        BigDecimal fiatAmount = cryptoAmount.multiply(rate);

        subtractCrypto(cryptoWallet, crypto, cryptoAmount);
        addFiat(bankAccount, fiat, fiatAmount);

        try {
            bankAccountProxy.updateAccount(bankAccount);
            cryptoWalletProxy.updateWallet(cryptoWallet);
        } catch (FeignException exception) {
            return "Došlo je do greške prilikom ažuriranja računa ili crypto wallet-a.";
        }

        return "Uspešno je izvršena prodaja "
                + cryptoAmount + " " + crypto
                + " za " + fiatAmount + " " + fiat
                + ". Novo stanje računa: " + bankAccount
                + ". Novo stanje crypto wallet-a: " + cryptoWallet;
    }

    private BigDecimal getTradeRate(String crypto, String fiat) {

        TradeModel trade = tradeRepository.findByCryptoAndFiat(crypto, fiat);

        if (trade == null) {
            return null;
        }

        return BigDecimal.valueOf(trade.getRate());
    }

    private boolean isFiat(String currency) {
        return currency.equals("EUR")
                || currency.equals("USD")
                || currency.equals("GBP")
                || currency.equals("CHF")
                || currency.equals("RSD");
    }

    private boolean isCrypto(String currency) {
        return currency.equals("BTC")
                || currency.equals("ETH")
                || currency.equals("SOL");
    }

    private BigDecimal getFiatBalance(
            BankAccountDto account,
            String currency) {

        switch (currency) {
            case "EUR":
                return valueOrZero(account.getEur());

            case "USD":
                return valueOrZero(account.getUsd());

            case "GBP":
                return valueOrZero(account.getGbp());

            case "CHF":
                return valueOrZero(account.getChf());

            case "RSD":
                return valueOrZero(account.getRsd());

            default:
                return BigDecimal.ZERO;
        }
    }

    private void subtractFiat(
            BankAccountDto account,
            String currency,
            BigDecimal amount) {

        switch (currency) {
            case "EUR":
                account.setEur(valueOrZero(account.getEur()).subtract(amount));
                break;

            case "USD":
                account.setUsd(valueOrZero(account.getUsd()).subtract(amount));
                break;

            case "GBP":
                account.setGbp(valueOrZero(account.getGbp()).subtract(amount));
                break;

            case "CHF":
                account.setChf(valueOrZero(account.getChf()).subtract(amount));
                break;

            case "RSD":
                account.setRsd(valueOrZero(account.getRsd()).subtract(amount));
                break;
        }
    }

    private void addFiat(
            BankAccountDto account,
            String currency,
            BigDecimal amount) {

        switch (currency) {
            case "EUR":
                account.setEur(valueOrZero(account.getEur()).add(amount));
                break;

            case "USD":
                account.setUsd(valueOrZero(account.getUsd()).add(amount));
                break;

            case "GBP":
                account.setGbp(valueOrZero(account.getGbp()).add(amount));
                break;

            case "CHF":
                account.setChf(valueOrZero(account.getChf()).add(amount));
                break;

            case "RSD":
                account.setRsd(valueOrZero(account.getRsd()).add(amount));
                break;
        }
    }

    private BigDecimal getCryptoBalance(
            CryptoWalletDto wallet,
            String crypto) {

        switch (crypto) {
            case "BTC":
                return valueOrZero(wallet.getBtc());

            case "ETH":
                return valueOrZero(wallet.getEth());

            case "SOL":
                return valueOrZero(wallet.getSol());

            default:
                return BigDecimal.ZERO;
        }
    }

    private void subtractCrypto(
            CryptoWalletDto wallet,
            String crypto,
            BigDecimal amount) {

        switch (crypto) {
            case "BTC":
                wallet.setBtc(valueOrZero(wallet.getBtc()).subtract(amount));
                break;

            case "ETH":
                wallet.setEth(valueOrZero(wallet.getEth()).subtract(amount));
                break;

            case "SOL":
                wallet.setSol(valueOrZero(wallet.getSol()).subtract(amount));
                break;
        }
    }

    private void addCrypto(
            CryptoWalletDto wallet,
            String crypto,
            BigDecimal amount) {

        switch (crypto) {
            case "BTC":
                wallet.setBtc(valueOrZero(wallet.getBtc()).add(amount));
                break;

            case "ETH":
                wallet.setEth(valueOrZero(wallet.getEth()).add(amount));
                break;

            case "SOL":
                wallet.setSol(valueOrZero(wallet.getSol()).add(amount));
                break;
        }
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}