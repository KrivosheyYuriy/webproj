package org.example.webbproj.dao.userDao;

import org.example.webbproj.entity.User;
import org.example.webbproj.util.PasswordUtil;
import org.flywaydb.core.internal.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl implements UserDao {
    private final Connection connection;

    public UserDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User findUserById(long id) throws SQLException {
        String sql = "select * from users where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractUser(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public User findUserByUsername(String username) throws SQLException {
        String sql = "select * from users where username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractUser(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public Pair<String, String> createUser() throws SQLException {
        String sql = "insert into users(username, password) values (?, ?)";
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
    public void updateUser(User user, long id) throws SQLException {
        String sql = "update users set username = ?, password = ? where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setLong(3, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteUserById(long id) throws SQLException {
        String sql = "delete from users where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    private User extractUser(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        return new User(id, username, password);
    }

    private long countUsers() throws SQLException {
        String sql = "select count(*) from users";
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
