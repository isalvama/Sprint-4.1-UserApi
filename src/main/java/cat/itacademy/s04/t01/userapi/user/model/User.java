package cat.itacademy.s04.t01.userapi.user.model;

import cat.itacademy.s04.t01.userapi.user.exception.InvalidUserException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import java.util.UUID;

@Getter
public class User {

    private final UUID uuid;

    @NotBlank(message = "Name cannot be blank")
    private final String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private final String email;

    public User(String name, String email, UUID uuid) {
        if (name == null || name.isBlank()) {
            throw new InvalidUserException("User's Name can not be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new InvalidUserException("User's Email can not be null or blank");
        }
        if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")){
            throw new InvalidUserException("User's Email is not valid");
        }
        if (uuid == null) {
            throw new InvalidUserException("User's Id can not be null");
        }
        this.name = name;
        this.email = email;
        this.uuid = uuid;
    }
}
