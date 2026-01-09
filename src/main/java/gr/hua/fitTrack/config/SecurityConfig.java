package gr.hua.fitTrack.config;

import gr.hua.fitTrack.core.repository.PersonRepository;
import gr.hua.fitTrack.core.security.filters.CookieFilter;
import gr.hua.fitTrack.core.security.filters.JwtAuthenticationFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@EnableWebSecurity
@Profile("!dev")

public class SecurityConfig {

    private final PersonRepository personRepository;

    public SecurityConfig(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


    /**
     * @code "/api/**" (stateless, jwt-based authentication)
     * @param http
     * @param jwtAuthenticationFilter
     * @return
     */

    @Bean
    @Order(1)
    public SecurityFilterChain apiChain(final HttpSecurity http, final JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .securityMatcher("/api/v1/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/client-tokens").permitAll()
                        .requestMatchers("/api/v1/**").authenticated()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);
        return http.build();


    }


    /**
     * stateful, cookie-based authentication
     * @param http
     * @return
     * @throws Exception
     *
     *
     */

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {




        http
                .securityMatcher("/**")
                .addFilterBefore(new CookieFilter(personRepository), AuthenticationFilter.class)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/v3/api-docs/**","/swagger-ui.html","/swagger-ui/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/verifyPhone/**", "/deletePerson/**","/register/**","/login/**",
                                "/clientProfileCreation/**", "/trainerProfileCreation/**","/register","/trainers",
                                "/","/login", "/css/**", "/js/**", "/images/**","/v3/api-docs/**",
                                "/swagger-ui.html","/swagger-ui/**","/error/**").permitAll()
                        .requestMatchers("/trainerProfileCreation/**").hasRole("TRAINER")
                        .requestMatchers("/clientProfileCreation/**").hasRole("CLIENT")
                        .requestMatchers(
                                "/loginHomepage",
                                "/loginHomepage/**",
                                "/trainer/profile/**",
                                "/client/profile/**"
                        ).authenticated()


                )

                .formLogin(form -> form
                        .loginPage("/login")      // <-- YOUR CUSTOM PAGE
                        .loginProcessingUrl("/login") // the POST URL
                        .successHandler((request, response, authentication) -> {
                            Cookie mailCookie = new Cookie("mail", request.getParameter("username"));
                            mailCookie.setPath("/");
                            response.addCookie(mailCookie);

                            response.sendRedirect("/loginHomepage");
                        })
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID","mail")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .httpBasic(HttpBasicConfigurer::disable);

        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

/*        @Bean
        public FilterRegistrationBean<JwtAuthenticationFilter> registration(JwtAuthenticationFilter filter) {
            FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
            registration.setEnabled(false); // Disables automatic registration as a global filter
            return registration;
        }
*\

    /* temporarily disabled
    // Temporary in-memory user
    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        UserDetails user = User.builder()
                .username("test")
                .password(encoder.encode("1234"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }*/
}
