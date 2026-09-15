package cryptoWallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"api.proxies", "cryptoWallet"})
public class CryptoWalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoWalletApplication.class, args);
    }
}