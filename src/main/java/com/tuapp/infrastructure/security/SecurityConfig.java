package com.tuapp.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/auth/login",
                    "/auth/register",
                    "/auth/logout",
                    "/api/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**",
                    "/error"
                ).permitAll()
                .requestMatchers("/dashboard/users/**").hasAnyRole("SUPER_USER", "ADMIN")
                .requestMatchers("/dashboard/instituciones/**").hasAnyRole("SUPER_USER", "ADMIN")
                .requestMatchers("/dashboard/focos/**").hasAnyRole("SUPER_USER", "ADMIN")
                .requestMatchers("/dashboard/categorias-anomalias/**").hasAnyRole("SUPER_USER", "ADMIN")
                .requestMatchers("/dashboard/anomalias/**").hasAnyRole("SUPER_USER", "ADMIN")
                .requestMatchers("/dashboard/enfermedades/**").hasAnyRole("SUPER_USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    String uri = request.getRequestURI();

                    if (uri.equals("/auth/login") || uri.equals("/auth/register")) {
                        response.setStatus(200);
                        return;
                    }

                    response.sendRedirect("/auth/login");
                })
            )
            .addFilterBefore(
                new JwtAuthFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}