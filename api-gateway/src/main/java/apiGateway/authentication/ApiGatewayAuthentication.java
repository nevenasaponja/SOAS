package apiGateway.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import api.dtos.UserDto;

@Configuration
@EnableWebFluxSecurity
public class ApiGatewayAuthentication {

    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeExchange(exchange -> exchange

                        // CURRENCY EXCHANGE
                        .pathMatchers("/currency-exchange/**")
                        .hasAnyRole("OWNER", "ADMIN", "USER")

                        // CRYPTO EXCHANGE
                        .pathMatchers("/crypto-exchange/**")
                        .hasAnyRole("OWNER", "ADMIN", "USER")

                        // CURRENCY CONVERSION
                        .pathMatchers("/currency-conversion/**")
                        .hasRole("USER")

                        // CRYPTO CONVERSION
                        .pathMatchers("/crypto-conversion/**")
                        .hasRole("USER")


                        // =========================
                        // BANK ACCOUNT
                        // =========================

                        // USER moze samo svoj racun
                        .pathMatchers(
                                HttpMethod.GET,
                                "/bank-accounts/my-account")
                        .hasRole("USER")

                        // ADMIN moze da pregleda sve racune
                        .pathMatchers(
                                HttpMethod.GET,
                                "/bank-accounts")
                        .hasRole("ADMIN")

                        // ADMIN moze da pregleda racun po email-u
                        .pathMatchers(
                                HttpMethod.GET,
                                "/bank-accounts/email")
                        .hasRole("ADMIN")

                        // ADMIN moze da napravi racun
                        .pathMatchers(
                                HttpMethod.POST,
                                "/bank-accounts")
                        .hasRole("ADMIN")

                        // ADMIN moze da azurira racun
                        .pathMatchers(
                                HttpMethod.PUT,
                                "/bank-accounts")
                        .hasRole("ADMIN")

                        // Brisanje se radi interno iz UsersService
                        .pathMatchers(
                                HttpMethod.DELETE,
                                "/bank-accounts")
                        .denyAll()

                        // Sve ostalo za Bank Account zabranjeno
                        .pathMatchers("/bank-accounts/**")
                        .denyAll()


                        // =========================
                        // CRYPTO WALLET
                        // =========================

                        // USER moze samo svoj wallet
                        .pathMatchers(
                                HttpMethod.GET,
                                "/crypto-wallets/my-wallet")
                        .hasRole("USER")

                        // ADMIN moze da pregleda sve wallet-e
                        .pathMatchers(
                                HttpMethod.GET,
                                "/crypto-wallets")
                        .hasRole("ADMIN")

                        // ADMIN moze da pregleda wallet po email-u
                        .pathMatchers(
                                HttpMethod.GET,
                                "/crypto-wallets/email")
                        .hasRole("ADMIN")

                        // ADMIN moze da napravi wallet
                        .pathMatchers(
                                HttpMethod.POST,
                                "/crypto-wallets")
                        .hasRole("ADMIN")

                        // ADMIN moze da azurira wallet
                        .pathMatchers(
                                HttpMethod.PUT,
                                "/crypto-wallets")
                        .hasRole("ADMIN")

                        // Brisanje se radi interno iz UsersService
                        .pathMatchers(
                                HttpMethod.DELETE,
                                "/crypto-wallets")
                        .denyAll()

                        // Sve ostalo za Crypto Wallet zabranjeno
                        .pathMatchers("/crypto-wallets/**")
                        .denyAll()


                        // TRADE SERVICE
                        .pathMatchers("/trade-service/**")
                        .hasRole("USER")


                        // =========================
                        // USERS SERVICE
                        // =========================

                        .pathMatchers(
                                HttpMethod.POST,
                                "/users/newOwner")
                        .hasRole("OWNER")

                        .pathMatchers(
                                HttpMethod.POST,
                                "/users/newAdmin")
                        .hasRole("OWNER")

                        .pathMatchers(
                                HttpMethod.POST,
                                "/users/newUser")
                        .hasAnyRole("OWNER", "ADMIN")

                        .pathMatchers(
                                HttpMethod.PUT,
                                "/users")
                        .hasAnyRole("OWNER", "ADMIN")

                        .pathMatchers(
                                HttpMethod.DELETE,
                                "/users")
                        .hasRole("OWNER")

                        .pathMatchers(
                                HttpMethod.GET,
                                "/users")
                        .hasAnyRole("OWNER", "ADMIN")

                        .pathMatchers(
                                HttpMethod.GET,
                                "/users/email")
                        .hasAnyRole("OWNER", "ADMIN")

                        .pathMatchers("/users/**")
                        .hasRole("OWNER")

                        .anyExchange()
                        .authenticated()
                )

                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    ReactiveUserDetailsService reactiveUserDetailsService(
            WebClient.Builder webClientBuilder,
            BCryptPasswordEncoder encoder) {

        WebClient client = webClientBuilder
                .baseUrl("http://users-service:8770")
                .build();

        return user -> client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/email")
                        .queryParam("email", user)
                        .build())
                .retrieve()
                .bodyToMono(UserDto.class)
                .map(dto -> User
                        .withUsername(dto.getEmail())
                        .password(encoder.encode(dto.getPassword()))
                        .roles(dto.getRole())
                        .build());
    }

    @Bean
    BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}