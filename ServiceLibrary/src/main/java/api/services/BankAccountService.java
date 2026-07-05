package api.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import api.dtos.BankAccountDto;

public interface BankAccountService {

    @GetMapping("/bank-accounts")
    List<BankAccountDto> getAccounts();

    @GetMapping("/bank-accounts/email")
    BankAccountDto getAccountByEmail(@RequestParam String email);

    @PostMapping("/bank-accounts")
    ResponseEntity<?> createAccount(@RequestBody BankAccountDto dto);

    @PutMapping("/bank-accounts")
    ResponseEntity<?> updateAccount(@RequestBody BankAccountDto dto);

    @DeleteMapping("/bank-accounts")
    ResponseEntity<?> deleteAccount(@RequestParam String email);
}