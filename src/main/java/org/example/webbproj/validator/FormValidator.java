package org.example.webbproj.validator;

public class FormValidator {
    public static String validateForm(String fullName, String phone, String email) {
        StringBuilder errors = new StringBuilder();

        if (fullName == null) {
            errors.append("Full name is not filled in\n");
        }
        else if (!FormValidator.isValidFullName(fullName)) {
            errors.append("Wrong format of full name\n");
        }

        if (phone == null) {
            errors.append("Phone is not filled in\n");
        }
        else if (!FormValidator.isValidPhone(phone)) {
            errors.append("Wrong format of phone\n");
        }

        if (email == null) {
            errors.append("Email is not filled in\n");
        }
        else if (!FormValidator.isValidEmail(email)) {
            errors.append("Wrong format of email\n");
        }

        return errors.toString();
    }

    private static boolean isValidPhone(final String phone) {
        return phone.matches("^(\\+7|8)[0-9]{10}$");
    }

    private static boolean isValidEmail(final String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private static boolean isValidFullName(final String fullName) {
        return fullName.matches("^[А-ЯЁ][а-яё]+(\\s[А-ЯЁ][а-яё]+){1,2}$");
    }
}
