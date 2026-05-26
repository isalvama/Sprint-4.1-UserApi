package cat.itacademy.s04.t01.userapi.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String identifierName, String identifierValue) {
        super(String.format("User not found with %s: %s", identifierName, identifierValue));
    }
}
