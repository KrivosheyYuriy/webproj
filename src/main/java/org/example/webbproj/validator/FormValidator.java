package org.example.webbproj.validator;

import org.example.webbproj.entity.Form;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class FormValidator {
    public static String validateForm(Form form) {
        String fullName = form.getFullName();
        String email = form.getEmail();
        String phone = form.getPhone();
        String gender = form.getGender();
        Date birthday = form.getBirthday();
        List<Long> languages = form.getLanguagesId();

        StringBuilder errors = new StringBuilder();

        if (fullName == null) {
            errors.append("Full name is not filled in\n");
        }
        else if (!isValidFullName(fullName)) {
            errors.append("Wrong format of full name\n");
        }

        if (phone == null) {
            errors.append("Phone is not filled in\n");
        }
        else if (!isValidPhone(phone)) {
            errors.append("Wrong format of phone\n");
        }

        if (email == null) {
            errors.append("Email is not filled in\n");
        }
        else if (!isValidEmail(email)) {
            errors.append("Wrong format of email\n");
        }

        if (gender == null) {
            errors.append("Gender is not filled in\n");
        }
        else if (!isValidBirthday(LocalDate.ofInstant(birthday.toInstant(), TimeZone.getDefault().toZoneId()))) {
            errors.append("Wrong format of birthday\n");
        }

        if (languages == null) {
            errors.append("Languages is not filled in\n");
        }
        else if(languages.isEmpty()) {
            errors.append("Languages should not be empty\n");
        }
        else if (languages.stream().mapToLong(l -> l).min().getAsLong() < 1 ||
                languages.stream().mapToLong(l -> l).max().getAsLong() > 11) {
            errors.append("Language with not validId\n");
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

    private static boolean isValidBirthday(final LocalDate birthday) {
        LocalDate earliestPossibleBirthday = LocalDate.now().minusYears(110);
        return birthday.isBefore(LocalDate.now()) && birthday.isAfter(earliestPossibleBirthday);
    }
}
