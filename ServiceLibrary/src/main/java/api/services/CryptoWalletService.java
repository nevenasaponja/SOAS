package api.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import api.dtos.CryptoWalletDto;

public interface CryptoWalletService {

    @GetMapping("/crypto-wallets")
    ResponseEntity<?> getWallets();

    @GetMapping("/crypto-wallets/email")
    ResponseEntity<?> getWalletByEmail(@RequestParam String email);

    @GetMapping("/crypto-wallets/my-wallet")
    ResponseEntity<?> getMyWallet(
            @RequestHeader("Authorization") String authorization);

    @PostMapping("/crypto-wallets")
    ResponseEntity<?> createWallet(@RequestBody CryptoWalletDto wallet);

    @PutMapping("/crypto-wallets")
    ResponseEntity<?> updateWallet(@RequestBody CryptoWalletDto wallet);

    @DeleteMapping("/crypto-wallets")
    ResponseEntity<?> deleteWallet(@RequestParam String email);
}