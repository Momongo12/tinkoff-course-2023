package momongo12.fintech.api.controllers.exceptions;

import lombok.experimental.StandardException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Momongo12
 * @version 1.0
 */
@StandardException
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {
}
