package cat.itacademy.s04.t01.userapi.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String identifierName, String identifierValue) {
        super(String.format("User not found with %s: %s", identifierName, identifierValue));
    }
}
