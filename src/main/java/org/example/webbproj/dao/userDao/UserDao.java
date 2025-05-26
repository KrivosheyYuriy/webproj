package org.example.webbproj.dao.userDao;

import org.example.webbproj.entity.User;
import org.flywaydb.core.internal.util.Pair;

import java.sql.SQLException;

public interface UserDao {
    User findUserById(long id) throws SQLException;
    User findUserByUsername(String username) throws SQLException;
    Pair<String, String> createUser() throws SQLException;
    void updateUser(User user, long id) throws SQLException;
    void deleteUserById(long id) throws SQLException;
}