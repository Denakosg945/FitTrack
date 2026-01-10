package gr.hua.fitTrack.config;

import gr.hua.fitTrack.core.repository.ClientProfileRepository;
import gr.hua.fitTrack.core.repository.PersonRepository;
import gr.hua.fitTrack.core.service.ClientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@Profile("dev")
public class DevSecurityConfig {

    private final ClientProfileRepository clientRepository;

    public DevSecurityConfig(ClientProfileRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Bean
    public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new DevAuthFilter(clientRepository),
                org.springframework.security.web.access.intercept.AuthorizationFilter.class);



        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static class DevAuthFilter extends OncePerRequestFilter {
        private final ClientProfileRepository clientRepository;

        public DevAuthFilter(ClientProfileRepository clientRepository) {
            this.clientRepository = clientRepository;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            // Pick an email that exists in DB
            String dummyEmail = clientRepository.findAll().getFirst().getPerson().getEmailAddress();

            try {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        dummyEmail,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                System.out.println("Dev Auth Error: " + e.getMessage());
            }

            filterChain.doFilter(request, response);
        }
    }
}

