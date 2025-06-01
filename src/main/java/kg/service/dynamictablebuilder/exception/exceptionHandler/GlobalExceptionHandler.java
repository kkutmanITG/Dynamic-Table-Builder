package kg.service.dynamictablebuilder.exception.exceptionHandler;

import kg.service.dynamictablebuilder.exception.exceptionResponse.ExceptionResponse;
import kg.service.dynamictablebuilder.exception.exceptions.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResponse badRequest(BadRequestException exception) {
        return ExceptionResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .exceptionClassName(exception.getClass().getSimpleName())
                .message(exception.getMessage())
                .build();
    }
}
