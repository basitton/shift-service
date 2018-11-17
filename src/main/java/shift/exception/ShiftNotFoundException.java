package shift.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShiftNotFoundException extends IllegalArgumentException {
    public ShiftNotFoundException(String message) {
        super(message);
    }
}
