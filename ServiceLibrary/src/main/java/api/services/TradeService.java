package api.services;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface TradeService {

    @GetMapping("/trade-service")
    String trade(
            @RequestParam String email,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal quantity);
}