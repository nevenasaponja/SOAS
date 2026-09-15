package bankAccount;

import org.springframework.cloud.openfeign.FeignClient;

import api.services.UsersService;

@FeignClient("users-service")
public interface UsersProxy extends UsersService {
}