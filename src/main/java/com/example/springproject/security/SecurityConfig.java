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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //AuthenticationManager 是 Spring Security 内部的东西 用来验证密码
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Autowired
    private JwtFilter jwtFilter;

    // 告诉 Spring Security 用 BCrypt 来验证密码 ----你这个java class中没有调用它，但是spring security内部代码自动调用了
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/hello", "/swagger-ui/**", "/v3/api-docs/**","/v3/api-docs", "/error" ).permitAll()
                        .anyRequest().authenticated()                        // 其余anyRequest都要authenticated登录
                )
                .csrf(csrf -> csrf.disable()) // 测试时候 可以disable CSRF（Cross-Site Request Forgery，跨站请求伪造）是针对 Cookie 登录 的攻击
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))// 防伪造攻击，开发先关掉 ，关掉 csrf，不然 POST 请求会被拦截
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)// ← 加这行 把 JwtFilter 插在默认Filter（UsernamePasswordAuthenticationFilter）前面
                .logout(x->x.disable()); //Spring Security 默认自己接管了 /logout 这个路径，你的 Controller 根本不会被执行到，直接被框架拦截返回 403。你自己写的 @GetMapping("/logout") 根本不会执行,选择B： 把你的接口改个名字，比如 /signout，完全绕开冲突：@GetMapping("/signout")

        return http.build();
    }
}
///hello  → 白名单，直接放行 → 不需要Session → 没有JSESSIONID
///test   → 需要登录 → Spring Security创建Session → 有JSESSIONID ✅
