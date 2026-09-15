package usersService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.CryptoWalletDto;
import api.dtos.UserDto;
import api.proxies.BankAccountProxy;
import api.proxies.CryptoWalletProxy;
import api.services.UsersService;

@RestController
public class UserServiceImpl implements UsersService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private BankAccountProxy bankAccountProxy;

    @Autowired
    private CryptoWalletProxy cryptoWalletProxy;


    @Override
    public List<UserDto> getUsers() {

        List<UserModel> models = repo.findAll();
        List<UserDto> dtos = new ArrayList<UserDto>();

        for (UserModel model : models) {
            dtos.add(convertModelToDto(model));
        }

        return dtos;
    }


    @Override
    public UserDto getUserByEmail(String email) {

        UserModel model = repo.findByEmail(email);

        if (model == null) {
            return null;
        }

        return convertModelToDto(model);
    }


    @Override
    public ResponseEntity<?> createOwner(UserDto dto) {

        List<UserModel> users = repo.findAll();

        for (UserModel user : users) {
            if (user.getRole().equals("OWNER")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Owner already exists");
            }
        }

        if (repo.findByEmail(dto.getEmail()) == null) {

            dto.setRole("OWNER");

            UserModel model = convertDtoToModel(dto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(repo.save(model));

        } else {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User with passed email already exists");
        }
    }


    @Override
    public ResponseEntity<?> createAdmin(UserDto dto) {

        if (repo.findByEmail(dto.getEmail()) == null) {

            dto.setRole("ADMIN");

            UserModel model = convertDtoToModel(dto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(repo.save(model));

        } else {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Admin with passed email already exists");
        }
    }


    @Override
    public ResponseEntity<?> createUser(UserDto dto) {

        if (repo.findByEmail(dto.getEmail()) == null) {

            dto.setRole("USER");

            UserModel model = convertDtoToModel(dto);

            UserModel savedUser = repo.save(model);

            bankAccountProxy.createAccount(
                    new BankAccountDto(dto.getEmail())
            );

            cryptoWalletProxy.createWallet(
                    new CryptoWalletDto(dto.getEmail())
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedUser);

        } else {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User with passed email already exists");
        }
    }


    @Override
    public ResponseEntity<?> updateUser(
            UserDto dto,
            String authorization) {

        UserModel targetUser = repo.findByEmail(dto.getEmail());

        // Korisnik koji se menja ne postoji
        if (targetUser == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User with passed email does not exist");
        }


        // Uzimamo email prijavljenog korisnika iz Basic Auth header-a
        String loggedUserEmail = getEmailFromAuthorization(authorization);

        if (loggedUserEmail == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid authorization");
        }


        // Pronalazimo prijavljenog korisnika
        UserModel loggedUser = repo.findByEmail(loggedUserEmail);

        if (loggedUser == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authenticated user does not exist");
        }


        // =====================================
        // OWNER
        // može da ažurira sve korisnike
        // =====================================
        if (loggedUser.getRole().equals("OWNER")) {

            repo.updateUser(
                    dto.getEmail(),
                    dto.getPassword(),
                    dto.getRole()
            );

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(dto);
        }


        // =====================================
        // ADMIN
        // može da menja samo USER korisnika
        // =====================================
        if (loggedUser.getRole().equals("ADMIN")) {

            // ADMIN pokušava da menja OWNER-a ili ADMIN-a
            if (!targetUser.getRole().equals("USER")) {

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("ADMIN can update only USER accounts");
            }


            // ADMIN ne sme USER-u da promeni ulogu
            if (!dto.getRole().equals("USER")) {

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("ADMIN cannot change USER role");
            }


            repo.updateUser(
                    dto.getEmail(),
                    dto.getPassword(),
                    "USER"
            );

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(dto);
        }


        // USER nema pravo pristupa
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("You do not have permission to update users");
    }


    @Override
    public ResponseEntity<?> deleteUser(String email) {

        UserModel user = repo.findByEmail(email);

        if (user != null) {

            if (user.getRole().equals("USER")) {

                bankAccountProxy.deleteAccount(email);
                cryptoWalletProxy.deleteWallet(email);
            }

            repo.delete(user);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("User deleted successfully");

        } else {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User with passed email does not exist");
        }
    }


    /*
     * Iz Authorization header-a:
     *
     * Basic xxxxxxxxx
     *
     * dobijamo:
     *
     * email:password
     *
     * i vraćamo samo email.
     */
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


    public UserDto convertModelToDto(UserModel model) {

        return new UserDto(
                model.getEmail(),
                model.getPassword(),
                model.getRole()
        );
    }


    public UserModel convertDtoToModel(UserDto dto) {

        return new UserModel(
                dto.getEmail(),
                dto.getPassword(),
                dto.getRole()
        );
    }
}