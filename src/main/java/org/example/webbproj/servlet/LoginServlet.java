package org.example.webbproj.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.webbproj.dao.adminDao.AdminDao;
import org.example.webbproj.dao.adminDao.AdminDaoImpl;
import org.example.webbproj.dao.userDao.UserDao;
import org.example.webbproj.dao.userDao.UserDaoImpl;
import org.example.webbproj.entity.Admin;
import org.example.webbproj.entity.User;
import org.example.webbproj.util.JWTUtil;
import org.example.webbproj.util.PasswordUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    private Connection connection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        connection = (Connection) context.getAttribute("dbConnection");
        if (connection == null) {
            throw new ServletException("No database connection found in ServletContext.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getContentType() != null && request.getContentType().toLowerCase().contains("application/json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                User userData = objectMapper.readValue(request.getInputStream(), User.class);
                if (userData != null) {
                    String username = userData.getUsername();
                    String password = userData.getPassword();

                    // Валидация данных (необходимо!)
                    if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().println("Username and password are required.");
                        return;
                    }

                    if (connection != null) {
                        UserDao userDao = new UserDaoImpl(connection);
                        AdminDao adminDao = new AdminDaoImpl(connection);
                        try {
                            User user = userDao.findUserByUsername(username);
                            Admin admin = adminDao.findAdminByUsername(username);
                            if (user == null && admin == null) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().println("Invalid credentials.");
                            }
                            else {
                                String hashedPassword = user != null ? user.getPassword() : admin.getPassword();
                                if (PasswordUtil.verifyPassword(password, hashedPassword, "")) {
                                    String token = JWTUtil.generateToken(username);

                                    // Отправка токена клиенту (в заголовке или в теле ответа)
                                    response.setStatus(HttpServletResponse.SC_OK);
                                    response.setHeader("Authorization", "Bearer " + token);
                                }
                                else {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.getWriter().println("Invalid credentials.");
                                }
                            }
                        }
                        catch (SQLException e) {
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            response.getWriter().println("Error logging in: " + e.getMessage());
                        }


                    }
                    else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        request.setAttribute("errors", "Internal Server Error");
                    }
                }
            }
            catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                response.getWriter().write("Error parsing JSON: " + e.getMessage());
            }
        }
        else {
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE); // 415 Unsupported Media Type
            response.getWriter().write("Unsupported Content-Type.  Expected application/json.");
        }
    }
}