package cat.itacademy.s04.t01.userapi.user.model;

import cat.itacademy.s04.t01.userapi.user.exception.InvalidUserException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private static final String NAME = "Maria";
    private static final String EMAIL = "m@mail.com";
    private static final UUID ID = UUID.randomUUID();


    private Validator validator;

    @Test
    @DisplayName("Should validate successfully when all fields are correct")
    void validate_success() {
       assertDoesNotThrow(() -> {
                   new User(NAME, EMAIL, UUID.randomUUID());
               }
       );
    }

    @Test
    @DisplayName("Should throw InvalidUserException when fields are null or blank")
    void validate_blankFields() {
        Exception exceptionBlankName = assertThrows(InvalidUserException.class, () -> {
                    new User("", EMAIL, ID);
                });
        assertTrue(exceptionBlankName.getMessage().contains("User's Name"));
        assertTrue(exceptionBlankName.getMessage().contains("blank"));

        Exception exceptionBlankEmail = assertThrows(InvalidUserException.class, () -> {
            new User(NAME, "", ID);
        });
        assertTrue(exceptionBlankEmail.getMessage().contains("User's Email"));
        assertTrue(exceptionBlankEmail.getMessage().contains("blank"));

        Exception exceptionNullId = assertThrows(InvalidUserException.class, () -> {
            new User(NAME, EMAIL, null);
        });
        assertTrue(exceptionNullId.getMessage().contains("User's Id"));
        assertTrue(exceptionNullId.getMessage().contains("null"));

    }

    @Test
    @DisplayName("Should throw InvalidUserException when email format is invalid")
    void validate_invalidEmail() {
        Exception exception = assertThrows(InvalidUserException.class, () -> {new User(NAME, "false-email", ID);
        });

        assertTrue(exception.getMessage().contains("User's Email"));
        assertTrue(exception.getMessage().contains("not valid"));

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