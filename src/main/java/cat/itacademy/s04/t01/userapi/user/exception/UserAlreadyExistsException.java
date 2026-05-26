package cat.itacademy.s04.t01.userapi.user.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("User with already exists with email: " + email);
    }
}
