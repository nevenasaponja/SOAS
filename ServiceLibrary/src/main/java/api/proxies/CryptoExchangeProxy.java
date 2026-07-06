package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CurrencyExchangeDto;

@FeignClient("crypto-exchange")
public interface CryptoExchangeProxy {

    @GetMapping("/crypto-exchange")
    ResponseEntity<CurrencyExchangeDto> getCryptoExchangeFeign(
            @RequestParam String from,
            @RequestParam String to
    );
}