package cat.itacademy.s04.t01.userapi.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class User {
    @NotNull
    private final UUID uuid;
    @NotNull
    @NotBlank(message = "Name cannot be blank")
    private final String name;
    @NotNull
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private final String email;

    public User(String name, String email, UUID uuid) {
        this.name = name;
        this.email = email;
        this.uuid = uuid;
    }
}
