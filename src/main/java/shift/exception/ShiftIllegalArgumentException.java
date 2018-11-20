package shift.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom application exception for unacceptable params or input
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class ShiftIllegalArgumentException extends IllegalArgumentException {
    public ShiftIllegalArgumentException(String message) {
        super(message);
    }
}
