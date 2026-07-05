package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import api.dtos.BankAccountDto;

@FeignClient("bank-account")
public interface BankAccountProxy {

    @GetMapping("/bank-accounts/email")
    BankAccountDto getAccountByEmail(@RequestParam String email);

    @PostMapping("/bank-accounts")
    ResponseEntity<?> createAccount(@RequestBody BankAccountDto dto);

    @PutMapping("/bank-accounts")
    BankAccountDto updateAccount(@RequestBody BankAccountDto dto);

    @DeleteMapping("/bank-accounts")
    String deleteAccount(@RequestParam String email);
}