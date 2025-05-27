import org.example.webbproj.util.PasswordUtil;
import org.example.webbproj.validator.FormValidator;
import org.junit.jupiter.api.Test;

public class FormValidatorTest extends FormValidator {
    @Test
    public void testFormValidator() {
        System.out.println(PasswordUtil.hashPassword("123", ""));
    }
}
