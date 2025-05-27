package org.example.webbproj.entity;

public class FormLanguages {
    private long formId;

    private long languageId;

    public FormLanguages(long formId, long languageId) {
        this.formId = formId;
        this.languageId = languageId;
    }
    
    public FormLanguages() {
    }

    public long getFormId() {
        return formId;
    }

    public long getLanguageId() {
        return languageId;
    }
}
