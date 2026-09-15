package cryptoExchange;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CurrencyExchangeDto;
import api.services.CryptoExchangeService;
import util.exceptions.CurrencyDoesntExistException;
import util.exceptions.NoDataFoundException;

@RestController
public class CryptoExchangeServiceImpl implements CryptoExchangeService {

    @Autowired
    private CryptoExchangeRepository repo;

    @Autowired
    private Environment environment;

    @Override
    @GetMapping("/crypto-exchange")
    public ResponseEntity<?> getCryptoExchange(
            @RequestParam String from,
            @RequestParam String to) {

        String missingCurrency = null;
        List<String> validCurrencies = repo.findAllDistinctCurrencis();

        if (!isValidCurrency(from)) {
            missingCurrency = from;
        } else if (!isValidCurrency(to)) {
            missingCurrency = to;
        }

        if (missingCurrency != null) {
            throw new CurrencyDoesntExistException(
                    String.format("Crypto currency %s does not exist in the database", missingCurrency),
                    validCurrencies
            );
        }

        CryptoExchangeModel dbResponse = repo.findByFromAndTo(from, to);

        if (dbResponse == null) {
            throw new NoDataFoundException(
                    String.format("Requested crypto exchange rate [%s to %s] does not exist", from, to),
                    validCurrencies
            );
        }

        CurrencyExchangeDto dto = new CurrencyExchangeDto(
                dbResponse.getFrom(),
                dbResponse.getTo(),
                dbResponse.getExchangeRate()
        );

        dto.setPort(environment.getProperty("local.server.port"));

        return ResponseEntity.ok(dto);
    }

    public boolean isValidCurrency(String currency) {
        List<String> currencies = repo.findAllDistinctCurrencis();

        for (String s : currencies) {
            if (s.equalsIgnoreCase(currency)) {
                return true;
            }
        }

        return false;
    }
}