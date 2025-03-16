package ro.unibuc.hello.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@ComponentScan("ro.unibuc.hello.service")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                // .authorizeHttpRequests(auth -> {
                //     auth.requestMatchers("/auth/**", "/hello-world").permitAll();
                //     auth.requestMatchers("/books/add").hasRole("ADMIN");
                //     auth.anyRequest().authenticated();
                // })
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/hello-world", "/availabletitle").permitAll()
                .requestMatchers("/books/add", "/books/all", "/books/update", "/books/delete", "/readers/all","/readers/borrowers","/readers/late","/readers/ban-late","/readers/banned","readers/un-ban").hasRole("ADMIN")
                .requestMatchers("/rent", "/return/**").hasRole("USER")
                .requestMatchers("/reserve/book", "/reserve/unreserve").hasRole("USER")
                .requestMatchers("/reserve/list/**").hasRole("ADMIN")
                .requestMatchers("/reserve/my-reservations").hasRole("USER")

                .anyRequest().authenticated()
            )
                .addFilterBefore(jwtRequestFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

