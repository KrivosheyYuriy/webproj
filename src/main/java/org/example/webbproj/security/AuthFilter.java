package org.example.webbproj.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webbproj.util.JWTUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebFilter("/*") // Фильтруем все запросы
public class AuthFilter implements Filter {

    private static final Set<String> ALLOWED_PATHS = new HashSet<>(List.of("/login")); // Пути, которые не требуют авторизации

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = getHttpServletResponse((HttpServletResponse) response);
        httpResponse.setStatus(HttpServletResponse.SC_OK);

        // Обрабатываем OPTIONS запросы
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK); // Или SC_NO_CONTENT
            return; // Важно: НЕ вызываем chain.doFilter() для OPTIONS!
        }

        String path = httpRequest.getRequestURI().substring(
                httpRequest.getContextPath().length()).replaceAll("[/]+$", ""); // Получаем путь запроса

        boolean allowedPath = ALLOWED_PATHS.contains(path) ||
                ("/form".equals(path) && httpRequest.getMethod().equals("POST")); // Проверяем, разрешен ли путь без авторизации
        if (allowedPath) {
            // Путь разрешен, пропускаем дальше
            chain.doFilter(request, response);
            return;
        }

        // Получаем токен из заголовка Authorization
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().println("Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7); // Извлекаем токен без "Bearer "

        try {
            String username = JWTUtil.verifyToken(token);
            // Добавляем пользователя в запрос, чтобы сервлет мог его использовать (необязательно, но удобно)

            HttpSession session = httpRequest.getSession();
            session.setAttribute("username", username);

            // Пропускаем запрос дальше
            chain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().println("Invalid JWT: " + e.getMessage());
        }
    }

    private static HttpServletResponse getHttpServletResponse(HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Expose-Headers", "Authorization"); // <--- ВАЖНО!
        return response;
    }

    @Override
    public void destroy() {
    }
}