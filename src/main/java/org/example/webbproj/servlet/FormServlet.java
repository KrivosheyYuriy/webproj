package org.example.webbproj.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webbproj.dao.formDao.FormDao;
import org.example.webbproj.dao.formDao.FormDaoImpl;
import org.example.webbproj.dao.userDao.UserDao;
import org.example.webbproj.dao.userDao.UserDaoImpl;
import org.example.webbproj.entity.Form;
import org.example.webbproj.entity.User;
import org.example.webbproj.response.UserData;
import org.example.webbproj.validator.FormValidator;
import org.flywaydb.core.internal.util.Pair;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "formServlet", value = "/form")
public class FormServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false); // Не создавать сессию, если ее нет
        if (session == null || session.getAttribute("username") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("Unauthorized: No username in session");
            return;
        }

        String username = (String) session.getAttribute("username");
        if (connection != null) {
            UserDao userDao = new UserDaoImpl(connection);
            FormDao formDao = new FormDaoImpl(connection);

            try {
                User user = userDao.findUserByUsername(username);
                long id = user.getId();

                Form form = formDao.findFormById(id);
                if (form == null) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    req.setAttribute("errors", "Internal Server Error");
                    return;
                }

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(form);
                resp.getWriter().write(json);
            } catch (SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                req.setAttribute("errors", "Internal Server Error");
            }
        }
        else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            req.setAttribute("errors", "Internal Server Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getContentType() != null && req.getContentType().toLowerCase().contains("application/json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Form formData = objectMapper.readValue(req.getInputStream(), Form.class);
                if (formData != null) {
                    String fullName = formData.getFullName();
                    String email = formData.getEmail();
                    String phone = formData.getPhone();
                    String taskDescription = formData.getTaskDescription();
                    String errors = FormValidator.validateForm(fullName, phone, email);

                    if (errors.isEmpty()) {

                        if (connection != null) {
                            FormDao formDao = new FormDaoImpl(connection);
                            UserDao userDao = new UserDaoImpl(connection);
                            try {
                                Pair<String, String> data = userDao.createUser();
                                long id = userDao.findUserByUsername(data.getLeft()).getId();

                                formDao.createForm(new Form(null, id, fullName, phone, email, taskDescription));
                                UserData userData = new UserData(data.getLeft(), data.getRight());

                                ObjectMapper mapper = new ObjectMapper();
                                String json = mapper.writeValueAsString(userData);
                                resp.getWriter().write(json);

                                resp.setStatus(HttpServletResponse.SC_CREATED);
                            } catch (SQLException e) {
                                System.err.println(e.getMessage());
                                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                req.setAttribute("errors", "Internal Server Error");
                            }
                        }
                        else {
                            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            req.setAttribute("errors", "Internal Server Error");
                        }
                    }
                    else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        req.setAttribute("errors", errors);
                    }
                }
                else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                    resp.getWriter().write("Invalid JSON format or empty request body.");
                }
            }
            catch (IOException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                resp.getWriter().write("Error parsing JSON: " + e.getMessage());
            }
        }
        else {
            resp.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE); // 415 Unsupported Media Type
            resp.getWriter().write("Unsupported Content-Type.  Expected application/json.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getContentType() != null && req.getContentType().toLowerCase().contains("application/json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Form data = objectMapper.readValue(req.getInputStream(), Form.class);

                if (data != null) {
                    String fullName = data.getFullName();
                    String email = data.getEmail();
                    String phone = data.getPhone();
                    String taskDescription = data.getTaskDescription();

                    HttpSession session = req.getSession(false); // Не создавать сессию, если ее нет
                    if (session == null || session.getAttribute("username") == null) {
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        resp.getWriter().println("Unauthorized: No username in session");
                        return;
                    }

                    String username = (String) session.getAttribute("username");

                    String errors = FormValidator.validateForm(fullName, phone, email);
                    if (errors.isEmpty()) {
                        if (connection != null) {
                            FormDao formDao = new FormDaoImpl(connection);
                            UserDao userDao = new UserDaoImpl(connection);
                            try {
                                User user = userDao.findUserByUsername(username);
                                if (user == null) {
                                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                    req.setAttribute("errors", "Internal Server Error");
                                    return;
                                }

                                long id = user.getId();
                                Form form = formDao.findFormById(id);
                                if (form == null) {
                                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                    req.setAttribute("errors", "Internal Server Error");
                                    return;
                                }

                                form.setEmail(email);
                                form.setPhone(phone);
                                form.setTaskDescription(taskDescription);
                                form.setFullName(fullName);

                                formDao.updateForm(form);
                                resp.setStatus(HttpServletResponse.SC_CREATED);
                            } catch (SQLException e) {
                                System.err.println(e.getMessage());
                                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                req.setAttribute("errors", "Internal Server Error");
                            }
                        }
                        else {
                            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            req.setAttribute("errors", "Internal Server Error");
                        }
                    }
                    else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        req.setAttribute("errors", errors);
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                    resp.getWriter().write("Invalid JSON format or empty request body.");
                }
            }
            catch (IOException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                resp.getWriter().write("Error parsing JSON: " + e.getMessage());
            }
        }
        else {
            // Если Content-Type не JSON, возвращаем ошибку
            resp.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE); // 415 Unsupported Media Type
            resp.getWriter().write("Unsupported Content-Type.  Expected application/json.");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Expose-Headers", "Authorization"); // <--- ВАЖНО!
        response.setStatus(HttpServletResponse.SC_OK);
    }
}