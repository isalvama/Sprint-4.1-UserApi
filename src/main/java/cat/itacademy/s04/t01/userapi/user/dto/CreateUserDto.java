package cat.itacademy.s04.t01.userapi.user.dto;


import jakarta.validation.constraints.*;
import org.jetbrains.annotations.NotNull;

public record CreateUserDto(
        @NotBlank(message = "Name cannot be empty")
        String name,

        @NotBlank(message = "The email cannot be empty")
        @Email(message = "The email format is not valid")
        String email) {
}
