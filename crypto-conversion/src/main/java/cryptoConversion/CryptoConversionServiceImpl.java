package cryptoConversion;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CryptoWalletDto;
import api.dtos.CurrencyExchangeDto;
import api.proxies.CryptoExchangeProxy;
import api.proxies.CryptoWalletProxy;
import api.services.CryptoConversionService;
import util.exceptions.CurrencyDoesntExistException;
import util.exceptions.InvalidQuantityException;

@RestController
public class CryptoConversionServiceImpl implements CryptoConversionService {

    @Autowired
    private CryptoExchangeProxy cryptoExchangeProxy;

    @Autowired
    private CryptoWalletProxy cryptoWalletProxy;

    @Override
    @GetMapping("/crypto-conversion")
    public ResponseEntity<?> getCryptoConversion(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal quantity) {

        return convertForUser(
                authorization,
                from,
                to,
                quantity
        );
    }

    @Override
    @GetMapping("/crypto-conversion/user")
    public ResponseEntity<?> convertForUser(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal quantity) {

        validateQuantity(quantity);

        String email = getEmailFromAuthorization(authorization);

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid authorization");
        }

        CurrencyExchangeDto exchange = cryptoExchangeProxy
                .getCryptoExchangeFeign(from, to)
                .getBody();

        if (exchange == null || exchange.getExchangeRate() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Crypto exchange rate does not exist");
        }

        CryptoWalletDto wallet =
                cryptoWalletProxy.getWalletByEmail(email);

        if (wallet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Crypto wallet does not exist");
        }

        BigDecimal currentFromAmount =
                getCryptoAmount(wallet, from);

        if (currentFromAmount.compareTo(quantity) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User does not have enough crypto on wallet");
        }

        BigDecimal convertedAmount =
                quantity.multiply(exchange.getExchangeRate());

        setCryptoAmount(
                wallet,
                from,
                currentFromAmount.subtract(quantity)
        );

        BigDecimal currentToAmount =
                getCryptoAmount(wallet, to);

        setCryptoAmount(
                wallet,
                to,
                currentToAmount.add(convertedAmount)
        );

        CryptoWalletDto updatedWallet =
                cryptoWalletProxy.updateWallet(wallet);

        String message =
                "Uspešno je izvršena razmena "
                        + quantity + " " + from.toUpperCase()
                        + " za "
                        + convertedAmount + " " + to.toUpperCase();

        return ResponseEntity.ok(
                message
                        + "\nNovo stanje novčanika: "
                        + updatedWallet
        );
    }

    private void validateQuantity(BigDecimal quantity) {

        if (quantity == null
                || quantity.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidQuantityException(
                    "Quantity must be greater than zero"
            );
        }
    }

    private BigDecimal getCryptoAmount(
            CryptoWalletDto wallet,
            String currency) {

        switch (currency.toUpperCase()) {

            case "BTC":
                return wallet.getBtc();

            case "ETH":
                return wallet.getEth();

            case "SOL":
                return wallet.getSol();

            default:
                throw new CurrencyDoesntExistException(
                        "Crypto currency " + currency
                                + " does not exist",
                        List.of("BTC", "ETH", "SOL")
                );
        }
    }

    private void setCryptoAmount(
            CryptoWalletDto wallet,
            String currency,
            BigDecimal value) {

        switch (currency.toUpperCase()) {

            case "BTC":
                wallet.setBtc(value);
                break;

            case "ETH":
                wallet.setEth(value);
                break;

            case "SOL":
                wallet.setSol(value);
                break;

            default:
                throw new CurrencyDoesntExistException(
                        "Crypto currency " + currency
                                + " does not exist",
                        List.of("BTC", "ETH", "SOL")
                );
        }
    }

    private String getEmailFromAuthorization(
            String authorization) {

        try {

            if (authorization == null
                    || !authorization.startsWith("Basic ")) {

                return null;
            }

            String encodedCredentials =
                    authorization.substring(6);

            byte[] decodedBytes =
                    Base64.getDecoder()
                            .decode(encodedCredentials);

            String decoded =
                    new String(
                            decodedBytes,
                            StandardCharsets.UTF_8);

            String[] credentials =
                    decoded.split(":", 2);

            if (credentials.length != 2) {
                return null;
            }

            return credentials[0];

        } catch (Exception e) {

            return null;
        }
    }
}