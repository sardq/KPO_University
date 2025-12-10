package demo.core.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.annotation.Generated;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final String adminRole = "ADMIN";
    private final String teacherRole = "TEACHER";
    private final String disciplineController = "/api/disciplines/**";
    private final String groupController = "/api/groups/**";
    private final String exerciseController = "/api/exercises/**";
    private final String gradeController = "/api/grades/**";

    public SecurityConfig(UserAuthenticationEntryPoint userAuthenticationEntryPoint,
            UserAuthenticationProvider userAuthenticationProvider) {
        this.userAuthenticationEntryPoint = userAuthenticationEntryPoint;
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(userAuthenticationEntryPoint))
                .addFilterBefore(new JwtAuthFilter(userAuthenticationProvider), BasicAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable()) // CSRF disabled because JWT is stateless NOSONAR
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/login", "/register", "/reset-password",
                                "/api/otp/**")
                        .permitAll()
                        .requestMatchers("/api/protocol/**")
                        .hasRole(teacherRole)
                        .requestMatchers(HttpMethod.POST, "/api/users/**")
                        .hasRole(adminRole)
                        .requestMatchers(HttpMethod.GET,
                                disciplineController,
                                groupController,
                                exerciseController,
                                gradeController)
                        .hasAnyRole("STUDENT", teacherRole, adminRole)
                        .requestMatchers(HttpMethod.POST,
                                disciplineController,
                                groupController,
                                exerciseController,
                                gradeController)
                        .hasAnyRole(teacherRole, adminRole)
                        .requestMatchers(HttpMethod.PUT,
                                disciplineController,
                                groupController,
                                exerciseController,
                                gradeController)
                        .hasAnyRole(teacherRole, adminRole)
                        .requestMatchers(HttpMethod.DELETE,
                                disciplineController,
                                groupController,
                                exerciseController,
                                gradeController)
                        .hasAnyRole(teacherRole, adminRole)
                        .requestMatchers("/api/user/me",
                                "/api/users/me")
                        .authenticated()
                        .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
