package org.example.webbproj.dao.adminDao;

import org.example.webbproj.entity.Admin;
import org.flywaydb.core.internal.util.Pair;

import java.sql.SQLException;

public interface AdminDao {
    Admin findAdminById(long id) throws SQLException;
    Admin findAdminByUsername(String username) throws SQLException;
    Pair<String, String> createAdmin() throws SQLException;
    void updateAdmin(Admin admin, long id) throws SQLException;
    void deleteAdminById(long id) throws SQLException;
}