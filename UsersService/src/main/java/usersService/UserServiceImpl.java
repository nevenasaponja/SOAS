package usersService;
import api.dtos.CryptoWalletDto;
import api.proxies.CryptoWalletProxy;

import java.util.ArrayList;
import api.dtos.BankAccountDto;
import api.proxies.BankAccountProxy;
import api.proxies.CryptoWalletProxy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.UserDto;
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
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body("Owner already exists");
			}
		}

		if (repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("OWNER");
			UserModel model = convertDtoToModel(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("User with passed email already exists");
		}
	}

	@Override
	public ResponseEntity<?> createAdmin(UserDto dto) {
		if (repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("ADMIN");
			UserModel model = convertDtoToModel(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Admin with passed email already exists");
		}
	}

	@Override
	public ResponseEntity<?> createUser(UserDto dto) {
	    if (repo.findByEmail(dto.getEmail()) == null) {
	        dto.setRole("USER");
	        UserModel model = convertDtoToModel(dto);
	        UserModel savedUser = repo.save(model);

	        bankAccountProxy.createAccount(new BankAccountDto(dto.getEmail()));
	        cryptoWalletProxy.createWallet(new CryptoWalletDto(dto.getEmail()));

	        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
	    } else {
	        return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body("User with passed email already exists");
	    }
	}

	@Override
	public ResponseEntity<?> updateUser(UserDto dto) {
		if (repo.findByEmail(dto.getEmail()) != null) {
			repo.updateUser(dto.getEmail(), dto.getPassword(), dto.getRole());
			return ResponseEntity.status(HttpStatus.OK).body(dto);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("User with passed email does not exist");
		}
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
	        return ResponseEntity.status(HttpStatus.OK)
	                .body("User deleted successfully");
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("User with passed email does not exist");
	    }
	}

	public UserDto convertModelToDto(UserModel model) {
		return new UserDto(model.getEmail(), model.getPassword(), model.getRole());
	}

	public UserModel convertDtoToModel(UserDto dto) {
		return new UserModel(dto.getEmail(), dto.getPassword(), dto.getRole());
	}
}