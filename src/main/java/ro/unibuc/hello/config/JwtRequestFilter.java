package ro.unibuc.hello.config;

import ro.unibuc.hello.service.*;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static ro.unibuc.hello.config.JwtUtil.SECRET_KEY;


@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ReaderService readerService;

    @Autowired
    private LibrarianService librarianService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Obține token-ul JWT din header
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Elimină "Bearer " din token

            // Extrage email-ul din token
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            // Dacă există utilizatorul în contextul Spring Security, nu face nimic
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Obține detalii despre utilizator
                var readerDetails = readerService.getReaderByEmail(email);
                var librarianDetails = librarianService.getLibrarianByEmail(email);

                if (readerDetails != null){
                    UserDetails userDetails = User.builder()
                            .username(readerDetails.getEmail())
                            .password(readerDetails.getPassword())
                            .build();
                // Dacă token-ul este valid, setează autentificarea în contextul de securitate
                    if (jwtUtil.validateToken(token, userDetails)) {
                        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + role);
                            var authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, authorities);
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } else if (librarianDetails != null){
                    UserDetails userDetails = User.builder()
                    .username(librarianDetails.getEmail())
                    .password(librarianDetails.getPassword())
                    .build();
                    // Dacă token-ul este valid, setează autentificarea în contextul de securitate
                        if (jwtUtil.validateToken(token, userDetails)) {
                            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + role);
                            var authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, authorities);
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
            }
        }

        filterChain.doFilter(request, response);
    }
}
