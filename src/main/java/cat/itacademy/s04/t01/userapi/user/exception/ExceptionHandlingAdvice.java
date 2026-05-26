package cat.itacademy.s04.t01.userapi.user.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlingAdvice {

    @ResponseBody
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExistsException (UserAlreadyExistsException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("User Already Exists");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException (UserNotFoundException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("User Not Found");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException (MethodArgumentNotValidException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Error in input data");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException (ConstraintViolationException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Error in Parameter");
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> errors.put(
                violation.getPropertyPath().toString(), violation.getMessage()
        ));
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }
}
