package kg.service.dynamictablebuilder.exception.exceptionResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
public class ExceptionResponse {
    private HttpStatus httpStatus;
    private String exceptionClassName;
    private String message;
}