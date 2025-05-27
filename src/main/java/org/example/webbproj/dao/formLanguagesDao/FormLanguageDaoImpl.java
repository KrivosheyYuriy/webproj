package org.example.webbproj.dao.formLanguagesDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FormLanguageDaoImpl implements FormLanguageDao {
    private final Connection connection;

    public FormLanguageDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Long> getLanguageIdsByFormId(long formId) throws SQLException {
        String sql = "SELECT language_id FROM form_languages WHERE form_id = ?";
        List<Long> languageIds = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, formId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    languageIds.add(rs.getLong("language_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching language IDs for form ID " + formId + ": " + e.getMessage());
            throw e; // Re-throw exception для обработки на верхнем уровне
        }

        return languageIds;
    }

    @Override
    public void updateFormLanguages(long formId, List<Long> languageIds) throws SQLException{
        deleteFormLanguages(formId);

        String sql = "INSERT INTO form_languages(form_id, language_id) values(?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Long languageId : languageIds) {
                statement.setLong(1, formId);
                statement.setLong(2, languageId);
                statement.executeUpdate();
            }
        }
    }

    private void deleteFormLanguages(long formId) throws SQLException {
        String sql = "DELETE FROM form_languages WHERE form_id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, formId);
            statement.executeUpdate();
        }
    }
}
