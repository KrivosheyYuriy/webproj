package org.example.webbproj.dao.adminDao;

import org.example.webbproj.entity.Admin;
import org.example.webbproj.util.PasswordUtil;
import org.flywaydb.core.internal.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDaoImpl implements AdminDao {
    private final Connection connection;

    public AdminDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Admin findAdminById(long id) throws SQLException {
        String sql = "select * from admins where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractAdmin(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public Admin findAdminByUsername(String username) throws SQLException {
        String sql = "select * from admins where username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractAdmin(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public Pair<String, String> createAdmin() throws SQLException {
        String sql = "insert into admins(username, password) values (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            String login = String.format("user%d", countUsers() + 1);
            String password = PasswordUtil.generateStrongPassword();

            preparedStatement.setString(1, login);
            preparedStatement.setString(2,
                    PasswordUtil.hashPassword(password, ""));
            preparedStatement.executeUpdate();
            return Pair.of(login, password);
        }
    }

    @Override
    public void updateAdmin(Admin admin, long id) throws SQLException {
        String sql = "update admins set username = ?, password = ? where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, admin.getUsername());
            preparedStatement.setString(2, admin.getPassword());
            preparedStatement.setLong(3, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteAdminById(long id) throws SQLException {
        String sql = "delete from admins where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    private Admin extractAdmin(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        return new Admin(id, username, password);
    }

    private long countUsers() throws SQLException {
        String sql = "select count(*) from admins";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        }
        return 0;
    }
}