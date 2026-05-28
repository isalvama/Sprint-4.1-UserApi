package cat.itacademy.s04.t01.userapi.user.exception;

public class InvalidUserException extends DomainException {
    public InvalidUserException(String message) {
        super(message);
    }
}
