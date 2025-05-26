package org.example.webbproj.dao.formDao;

import org.example.webbproj.entity.Form;

import java.sql.SQLException;

public interface FormDao {
    Form findFormById(long id) throws SQLException;
    void createForm(Form form) throws SQLException;
    void updateForm(Form form) throws SQLException;
    void deleteById(long id) throws SQLException;
}
