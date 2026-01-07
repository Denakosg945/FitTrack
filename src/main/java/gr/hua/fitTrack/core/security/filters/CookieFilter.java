package gr.hua.fitTrack.core.security.filters;

import gr.hua.fitTrack.core.repository.PersonRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class CookieFilter extends OncePerRequestFilter {
    private PersonRepository personRepository;

    public CookieFilter(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if(
                path.startsWith("/deletePerson") ||
                 path.startsWith("/verifyPhone") ||
                 path.startsWith("/clientProfileCreation") ||
                 path.startsWith("/trainerProfileCreation")){
            Cookie[] cookies = request.getCookies();
            String tokenFromCookie = null;

            if (cookies != null) {
                tokenFromCookie = Arrays.stream(cookies)
                        .filter(c -> "token".equals(c.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);
            }

            if(tokenFromCookie == null || !personRepository.existsByPhoneNumber(tokenFromCookie)){
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
