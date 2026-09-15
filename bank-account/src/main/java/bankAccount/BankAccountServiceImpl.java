package bankAccount;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import api.dtos.UserDto;

@RestController
public class BankAccountServiceImpl {

    @Autowired
    private BankAccountRepository repo;

    @Autowired
    private UsersProxy usersProxy;

    // ADMIN - pregled svih racuna
    @GetMapping("/bank-accounts")
    public List<BankAccountModel> getAccounts() {
        return repo.findAll();
    }

    // ADMIN i interni mikroservisi - pregled po email-u
    @GetMapping("/bank-accounts/email")
    public BankAccountModel getAccountByEmail(@RequestParam String email) {
        return repo.findByEmail(email);
    }

    // USER - pregled samo svog racuna
    @GetMapping("/bank-accounts/my-account")
    public ResponseEntity<?> getMyAccount(
            @RequestHeader("Authorization") String authorization) {

        String email = getEmailFromAuthorization(authorization);

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid authorization");
        }

        BankAccountModel account = repo.findByEmail(email);

        if (account == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Bank account does not exist");
        }

        return ResponseEntity.ok(account);
    }

    // Kreiranje racuna
    @PostMapping("/bank-accounts")
    public ResponseEntity<?> createAccount(
            @RequestBody BankAccountModel account) {

        if (account.getEmail() == null ||
                account.getEmail().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email is required");
        }

        if (repo.findByEmail(account.getEmail()) != null) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Bank account with passed email already exists");
        }

        // Provera da email pripada postojecem USER korisniku
        UserDto user;

        try {
            user = usersProxy.getUserByEmail(account.getEmail());
        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("User with passed email does not exist");
        }

        if (user == null || !"USER".equals(user.getRole())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Bank account can be created only for USER");
        }

        // Svaki novi racun pocinje od 0
        account.setEur(BigDecimal.ZERO);
        account.setUsd(BigDecimal.ZERO);
        account.setGbp(BigDecimal.ZERO);
        account.setChf(BigDecimal.ZERO);
        account.setRsd(BigDecimal.ZERO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repo.save(account));
    }

    // ADMIN - azuriranje racuna
    @PutMapping("/bank-accounts")
    public ResponseEntity<?> updateAccount(
            @RequestBody BankAccountModel account) {

        if (account.getEmail() == null ||
                account.getEmail().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email is required");
        }

        BankAccountModel existing =
                repo.findByEmail(account.getEmail());

        if (existing == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Bank account with passed email does not exist");
        }

        existing.setEur(account.getEur());
        existing.setUsd(account.getUsd());
        existing.setGbp(account.getGbp());
        existing.setChf(account.getChf());
        existing.setRsd(account.getRsd());

        return ResponseEntity.ok(repo.save(existing));
    }

    // Interno brisanje kada UsersService obrise USER-a
    @DeleteMapping("/bank-accounts")
    public ResponseEntity<?> deleteAccount(@RequestParam String email) {

        BankAccountModel existing = repo.findByEmail(email);

        if (existing == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Bank account with passed email does not exist");
        }

        repo.delete(existing);

        return ResponseEntity.ok(
                "Bank account deleted successfully"
        );
    }

    // Iz Basic Auth header-a uzima email prijavljenog korisnika
    private String getEmailFromAuthorization(String authorization) {

        try {

            if (authorization == null ||
                    !authorization.startsWith("Basic ")) {

                return null;
            }

            String encodedCredentials =
                    authorization.substring(6);

            byte[] decodedBytes =
                    Base64.getDecoder().decode(encodedCredentials);

            String decoded =
                    new String(decodedBytes, StandardCharsets.UTF_8);

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