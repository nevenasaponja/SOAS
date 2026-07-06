package apiGateway.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

	                    // Fiat exchange
	                    .pathMatchers("/currency-exchange/**")
	                    .hasAnyRole("OWNER", "ADMIN", "USER")

	                    // Crypto exchange
	                    .pathMatchers("/crypto-exchange/**")
	                    .hasAnyRole("OWNER", "ADMIN", "USER")

	                    // Fiat conversion
	                    .pathMatchers("/currency-conversion/**")
	                    .hasRole("USER")

	                    // Crypto conversion
	                    .pathMatchers("/crypto-conversion/**")
	                    .hasRole("USER")

	                    // Bank account
	                    .pathMatchers("/bank-accounts/**")
	                    .hasAnyRole("ADMIN", "USER")

	                    // Crypto wallet
	                    .pathMatchers("/crypto-wallets/**")
	                    .hasAnyRole("ADMIN", "USER")

	                    // Users
	                    .pathMatchers("/users/**")
	                    .hasAnyRole("OWNER", "ADMIN")

	                    .anyExchange()
	                    .authenticated()
	            )
	            .httpBasic(Customizer.withDefaults())
	            .build();
	}

    @Bean
    ReactiveUserDetailsService reactiveUserDetailsService(WebClient.Builder webClientBuilder,
                                                          BCryptPasswordEncoder encoder) {

        WebClient client = webClientBuilder
                .baseUrl("http://localhost:8770")
                .build();

        return user -> client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/email")
                        .queryParam("email", user)
                        .build())
                .retrieve()
                .bodyToMono(UserDto.class)
                .map(dto -> User.withUsername(dto.getEmail())
                        .password(encoder.encode(dto.getPassword()))
                        .roles(dto.getRole())
                        .build());
    }

    @Bean
    BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}