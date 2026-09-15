package cryptoWallet;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CryptoWalletDto;
import api.dtos.UserDto;
import api.services.CryptoWalletService;

@RestController
public class CryptoWalletServiceImpl implements CryptoWalletService {

    @Autowired
    private CryptoWalletRepository repo;

    @Autowired
    private UsersProxy usersProxy;

    @Override
    public ResponseEntity<?> getWallets() {
        return ResponseEntity.ok(repo.findAll());
    }

    @Override
    public ResponseEntity<?> getWalletByEmail(String email) {

        CryptoWalletModel wallet = repo.findByEmail(email);

        if (wallet == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Crypto wallet with passed email does not exist");
        }

        return ResponseEntity.ok(wallet);
    }

    @Override
    public ResponseEntity<?> getMyWallet(String authorization) {

        String email = getEmailFromAuthorization(authorization);

        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid authorization");
        }

        CryptoWalletModel wallet = repo.findByEmail(email);

        if (wallet == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Crypto wallet does not exist");
        }

        return ResponseEntity.ok(wallet);
    }

    @Override
    public ResponseEntity<?> createWallet(CryptoWalletDto walletDto) {

        if (walletDto.getEmail() == null ||
                walletDto.getEmail().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email is required");
        }

        if (repo.findByEmail(walletDto.getEmail()) != null) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Crypto wallet with passed email already exists");
        }

        UserDto user;

        try {
            user = usersProxy.getUserByEmail(walletDto.getEmail());
        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("User with passed email does not exist");
        }

        if (user == null || !"USER".equals(user.getRole())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Crypto wallet can be created only for USER");
        }

        CryptoWalletModel wallet = new CryptoWalletModel();

        wallet.setEmail(walletDto.getEmail());
        wallet.setBtc(BigDecimal.ZERO);
        wallet.setEth(BigDecimal.ZERO);
        wallet.setSol(BigDecimal.ZERO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repo.save(wallet));
    }

    @Override
    public ResponseEntity<?> updateWallet(CryptoWalletDto walletDto) {

        if (walletDto.getEmail() == null ||
                walletDto.getEmail().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email is required");
        }

        CryptoWalletModel existing =
                repo.findByEmail(walletDto.getEmail());

        if (existing == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Crypto wallet with passed email does not exist");
        }

        existing.setBtc(walletDto.getBtc());
        existing.setEth(walletDto.getEth());
        existing.setSol(walletDto.getSol());

        return ResponseEntity.ok(repo.save(existing));
    }

    @Override
    public ResponseEntity<?> deleteWallet(String email) {

        CryptoWalletModel existing = repo.findByEmail(email);

        if (existing == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Crypto wallet with passed email does not exist");
        }

        repo.delete(existing);

        return ResponseEntity.ok(
                "Crypto wallet deleted successfully"
        );
    }

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