package org.example.webbproj.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.webbproj.dao.formDao.FormDao;
import org.example.webbproj.dao.formDao.FormDaoImpl;
import org.example.webbproj.dao.formLanguagesDao.FormLanguageDao;
import org.example.webbproj.dao.formLanguagesDao.FormLanguageDaoImpl;
import org.example.webbproj.entity.Form;
import org.example.webbproj.validator.FormValidator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@WebServlet(name = "adminServlet", value = "/admin/*")
public class AdminServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        request.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            String[] pathParts = pathInfo.split("/");

            if (pathParts.length == 3 && pathParts[1].equals("form")) {
                try {
                    long id = Integer.parseInt(pathParts[2]);
                    if (connection != null) {
                        FormDao formDao = new FormDaoImpl(connection);
                        FormLanguageDao formLanguageDao = new FormLanguageDaoImpl(connection);
                        Form form = formDao.findFormById(id);
                        if (form == null) {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            request.setAttribute("errors", "Form not found.");
                            return;
                        }
                        List<Long> formLanguages = formLanguageDao.getLanguageIdsByFormId(form.getId());
                        form.setLanguagesId(formLanguages);

                        ObjectMapper mapper = new ObjectMapper();
                        String json = mapper.writeValueAsString(form);
                        response.getWriter().write(json);
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        request.setAttribute("errors", "Internal Server Error");
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("Invalid ID format.  Please provide an integer.");
                } catch (SQLException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().println("Internal Server Error");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Invalid URL format. Expected /admin/form/{id}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing ID.  Please provide /admin/form/{id}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        request.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            String[] pathParts = pathInfo.split("/");

            if (pathParts.length == 3 && pathParts[1].equals("form")) {
                try {
                    long id = Integer.parseInt(pathParts[2]);
                    if (request.getContentType() != null && request.getContentType().toLowerCase().contains("application/json")) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        Form data = objectMapper.readValue(request.getInputStream(), Form.class);

                        if (data != null) {
                            String fullName = data.getFullName();
                            String email = data.getEmail();
                            String phone = data.getPhone();
                            String taskDescription = data.getTaskDescription();
                            Date birthday = data.getBirthday();
                            String gender = data.getGender();
                            List<Long> languagesId = data.getLanguagesId();
                            String errors = FormValidator.validateForm(data);
                            if (errors.isEmpty()) {
                                if (connection != null) {
                                    FormDao formDao = new FormDaoImpl(connection);
                                    Form form = formDao.findFormById(id);
                                    FormLanguageDao formLanguageDao = new FormLanguageDaoImpl(connection);
                                    if (form == null) {
                                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                        request.setAttribute("errors", "Form not found");
                                        return;
                                    }

                                    form.setEmail(email);
                                    form.setPhone(phone);
                                    form.setTaskDescription(taskDescription);
                                    form.setFullName(fullName);
                                    form.setGender(gender);
                                    form.setLanguagesId(languagesId);
                                    form.setBirthday(birthday);

                                    formDao.updateForm(form);
                                    formLanguageDao.updateFormLanguages(id, languagesId);
                                    response.setStatus(HttpServletResponse.SC_CREATED);
                                } else {
                                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                    request.setAttribute("errors", "Internal Server Error");
                                }
                            } else {
                                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                request.setAttribute("errors", errors);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("Invalid ID format.  Please provide an integer.");
                } catch (SQLException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().println("Internal Server Error");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Invalid URL format. Expected /admin/form/{id}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing ID. Please provide /admin/form/{id}");
        }
    }
}
