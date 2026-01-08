package gr.hua.fitTrack.core.security.filters;

import gr.hua.fitTrack.core.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private void writeError(final HttpServletResponse response) throws IOException {
        // Invalid token or internal error
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + "Invalid_token" + "\"}");
    }

    public JwtAuthenticationFilter(final JwtService jwtService) {
        if (jwtService == null) throw new NullPointerException();
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ") || authorizationHeader.isBlank()){
            filterChain.doFilter(request, response);
            return;
        }


        final String token = authorizationHeader.substring(7);
        try {
            final Claims claims = this.jwtService.parse(token);
            final String subject = claims.getSubject();
            final Collection<String> roles = (Collection<String>) claims.get("roles");
            // Convert string to grantedAuthority
            final var authorities =
                    roles == null
                            ? List.<GrantedAuthority>of()
                            : roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
            // Create user
            final User principal = new User(subject,"",authorities);
            final UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(principal,null,authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }catch (Exception e) {
            this.writeError(response);
            return;
        }
        filterChain.doFilter(request, response); //next filter
    }
}
