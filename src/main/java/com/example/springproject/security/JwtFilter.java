package com.example.springproject.security
        ;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
//    你项目里已经有 JwtFilter 了，
//    它每次请求都把用户信息存进 SecurityContextHolder，
//    所以在 Controller 里直接用 Authentication 拿就行：
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 0. 白名单路径直接放行
        String path = request.getRequestURI();
        if (path.equals("/login") || path.equals("/register") || path.equals("/hello")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 开始
        // 1. 从 Header 取出 Token
        String authHeader = request.getHeader("Authorization");

        // 2. 检查 Token 格式是否正确
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7); // 去掉 "Bearer " 取出纯 Token

            // 3. 验证 Token
            if (jwtUtil.validateToken(token)) {

                // 4. 从 Token 取出用户名
                String username = jwtUtil.getUsername(token);

                // 5. 告诉 Spring Security 这个请求是谁发的
                UserDetails userDetails = userDetailsService.loadUserByUsername(username); //通过username找数据库的password
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(//就是创建一个认证对象，告诉 Spring Security：这个用户是谁 ✅ 他有什么权限 ✅ 他已经验证过了
                                userDetails,                    // 用户信息（用户名、密码、角色）
                                null,                           // 密码（已经验证过了，不需要再传）
                                userDetails.getAuthorities()    // 权限（USER角色）
                        );
                SecurityContextHolder //保险箱
                        .getContext() //打开保险箱
                        .setAuthentication(authentication);//把认证放进去 后面的 Filter 和 Controller 都能知道是谁 ✅
            } else {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("token失效了，可能是过期");
                return;
//                request.getHeader()   → 从请求里"拿"数据 ✅
//                response.setStatus()  → 往响应里"设置"数据 ✅
            }
        }

        // 6. 放行，继续走后面的流程
        filterChain.doFilter(request, response);
    }

    //登陆流程
    //1. 用户输入 username + password
    //2. 查数据库验证密码
    //3. 同时取出 role          ← 这步要确认有没有
    //4. 生成 JWT token 返回给客户端
}