package api.services;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public interface CryptoConversionService {

    @GetMapping("/crypto-conversion")
    ResponseEntity<?> getCryptoConversion(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal quantity);

    @GetMapping("/crypto-conversion/user")
    ResponseEntity<?> convertForUser(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal quantity);
}