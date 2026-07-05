package com.example.springproject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //AuthenticationManager is from Spring Security, it used to verify password
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Autowired
    private JwtFilter jwtFilter;

    // Tell Spring Security to use BCrypt for password verification---spring security used it internally
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:5500"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/hello", "/swagger-ui/**", "/v3/api-docs/**","/v3/api-docs", "/error" ).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))// 防伪造攻击，开发先关掉 ，关掉 csrf，不然 POST 请求会被拦截
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)// ← 加这行 把 JwtFilter 插在默认Filter（UsernamePasswordAuthenticationFilter）前面
                .logout(x->x.disable()); //Spring Security 默认自己接管了 /logout 这个路径，你的 Controller 根本不会被执行到，直接被框架拦截返回 403。你自己写的 @GetMapping("/logout") 根本不会执行,选择B： 把你的接口改个名字，比如 /signout，完全绕开冲突：@GetMapping("/signout")

        return http.build();
    }
}
///hello  → whilte list，allowed directly → no needSession → no JSESSIONID
///test   → need to login → Spring Security create Session → has JSESSIONID ✅
