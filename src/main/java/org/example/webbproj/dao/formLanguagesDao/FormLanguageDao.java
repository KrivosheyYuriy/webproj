package org.example.webbproj.dao.formLanguagesDao;

import java.sql.SQLException;
import java.util.List;

public interface FormLanguageDao {
    public void updateFormLanguages(long formId, List<Long> languageIds) throws SQLException;
    public List<Long> getLanguageIdsByFormId(long formId) throws SQLException;
}
