package org.example.webbproj.dao.formDao;

import org.example.webbproj.entity.Form;

import java.sql.*;

public class FormDaoImpl implements FormDao {
    private final Connection connection;

    public FormDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Form findFormByUserId(long id) throws SQLException {
        String sql = "select * from form where userid = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractForm(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public Form findFormById(long id) throws SQLException {
        String sql = "select * from form where id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractForm(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public void createForm(Form form) throws SQLException {
        String sql = "insert into form(userid, fullname, phone, email, taskdescription, gender, birthday) values(?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, form.getUserId());
            statement.setString(2, form.getFullName());
            statement.setString(3, form.getPhone());
            statement.setString(4, form.getEmail());
            statement.setString(5, form.getTaskDescription());
            statement.setString(6, form.getGender());
            statement.setDate(7, new Date(form.getBirthday().getTime()));
            System.out.println(statement);
            statement.execute();
        }
    }

    @Override
    public void updateForm(Form form) throws SQLException {
        String sql = "update form set fullName=?, phone=?, email=?, taskDescription=?, gender=?, birthday=? where id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, form.getFullName());
            statement.setString(2, form.getPhone());
            statement.setString(3, form.getEmail());
            statement.setString(4, form.getTaskDescription());
            statement.setString(5, form.getGender());
            statement.setDate(6, new Date(form.getBirthday().getTime()));
            statement.setLong(7, form.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteById(long id) throws SQLException {
        String sql = "delete from form where id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private Form extractForm(ResultSet resultSet) throws SQLException {
        return new Form(
                resultSet.getLong("id"),
                resultSet.getLong("userid"),
                resultSet.getString("fullName"),
                resultSet.getString("phone"),
                resultSet.getString("email"),
                resultSet.getString("taskDescription"),
                resultSet.getDate("birthday"),
                resultSet.getString("gender")
        );
    }
}
