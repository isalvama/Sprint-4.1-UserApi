package cat.itacademy.s04.t01.userapi.user.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should validate successfully when all fields are correct")
    void validate_success() {
        User user = new User("Alice", "alice@mail.com", UUID.randomUUID());

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "Should have no violations");
    }

    @Test
    @DisplayName("Should have violations when fields are null or blank")
    void validate_blankFields() {
        User user = new User("", "not-an-email", null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.size() >= 3);
    }

    @Test
    @DisplayName("Should fail when email format is invalid")
    void validate_invalidEmail() {
        User user = new User("Alice", "email-falso", UUID.randomUUID());

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        boolean hasEmailError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));

        assertTrue(hasEmailError, "Should have a violation for the email field");
    }

    @Test
    @DisplayName("Should fail when UUID is null")
    void validate_nullUuid() {
        User user = new User("Alice", "alice@mail.com", null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertEquals("uuid", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    @DisplayName("Constructor should assign values correctly")
    void constructor_assignsValues() {
        String name = "Bob";
        String email = "bob@mail.com";
        UUID uuid = UUID.randomUUID();

        User user = new User(name, email, uuid);

        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(uuid, user.getUuid());
    }
}