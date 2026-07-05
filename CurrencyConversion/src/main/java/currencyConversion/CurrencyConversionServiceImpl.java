package currencyConversion;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.CurrencyConversionDto;
import api.dtos.CurrencyExchangeDto;
import api.proxies.BankAccountProxy;
import api.proxies.CurrencyExchangeProxy;
import api.services.CurrencyConversionService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import util.exceptions.InvalidQuantityException;

@RestController
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    @Autowired
    private CurrencyExchangeProxy proxy;

    @Autowired
    private BankAccountProxy bankAccountProxy;

    Retry retry;
    CurrencyExchangeDto response;

    public CurrencyConversionServiceImpl(RetryRegistry registry) {
        retry = registry.retry("default");
    }

    @Override
    @CircuitBreaker(name = "cb", fallbackMethod = "fallback")
    public ResponseEntity<?> getConversionFeign(String from, String to, BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than zero");
        }

        retry.executeSupplier(() -> response = proxy.getExchangeFeign(from, to).getBody());

        CurrencyConversionDto finalResponse = new CurrencyConversionDto(response, quantity);
        finalResponse.setFeign(true);

        return ResponseEntity.ok(finalResponse);
    }

    public ResponseEntity<?> fallback(CallNotPermittedException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Currency conversion service is currently unavailable, Circuit breaker is in OPEN state!");
    }

    @Override
    public ResponseEntity<?> getConversion(String from, String to, BigDecimal quantity) {
        return getConversionFeign(from, to, quantity);
    }

    public ResponseEntity<?> convertForUser(String email, String from, String to, BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than zero");
        }

        CurrencyExchangeDto exchange = proxy.getExchangeFeign(from, to).getBody();
        BigDecimal convertedAmount = quantity.multiply(exchange.getExchangeRate());

        BankAccountDto account = bankAccountProxy.getAccountByEmail(email);

        BigDecimal currentFromAmount = getCurrencyAmount(account, from);

        if (currentFromAmount.compareTo(quantity) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User does not have enough money on bank account");
        }

        setCurrencyAmount(account, from, currentFromAmount.subtract(quantity));

        BigDecimal currentToAmount = getCurrencyAmount(account, to);
        setCurrencyAmount(account, to, currentToAmount.add(convertedAmount));

        BankAccountDto updatedAccount = bankAccountProxy.updateAccount(account);

        String message = "Uspešno je izvršena razmena " + from + ": " + quantity
                + " za " + to + ": " + convertedAmount;

        return ResponseEntity.ok(message + "\nNovo stanje računa: " + updatedAccount);
    }

    private BigDecimal getCurrencyAmount(BankAccountDto account, String currency) {
        switch (currency.toUpperCase()) {
            case "EUR":
                return account.getEur();
            case "USD":
                return account.getUsd();
            case "GBP":
                return account.getGbp();
            case "CHF":
                return account.getChf();
            case "RSD":
                return account.getRsd();
            default:
                throw new RuntimeException("Currency " + currency + " does not exist");
        }
    }

    private void setCurrencyAmount(BankAccountDto account, String currency, BigDecimal value) {
        switch (currency.toUpperCase()) {
            case "EUR":
                account.setEur(value);
                break;
            case "USD":
                account.setUsd(value);
                break;
            case "GBP":
                account.setGbp(value);
                break;
            case "CHF":
                account.setChf(value);
                break;
            case "RSD":
                account.setRsd(value);
                break;
            default:
                throw new RuntimeException("Currency " + currency + " does not exist");
        }
    }
}