package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import api.dtos.CryptoWalletDto;

@FeignClient("crypto-wallet")
public interface CryptoWalletProxy {

    @GetMapping("/crypto-wallets/email")
    CryptoWalletDto getWalletByEmail(@RequestParam String email);

    @PostMapping("/crypto-wallets")
    ResponseEntity<?> createWallet(@RequestBody CryptoWalletDto dto);

    @PutMapping("/crypto-wallets")
    CryptoWalletDto updateWallet(@RequestBody CryptoWalletDto dto);

    @DeleteMapping("/crypto-wallets")
    String deleteWallet(@RequestParam String email);
}