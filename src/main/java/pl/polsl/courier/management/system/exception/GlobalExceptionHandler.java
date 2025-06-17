package pl.polsl.courier.management.system.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Errors", description = "Globalne obsłużenie wyjątków z ErrorResponse")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Operation(summary = "Obsługa wszystkich nieoczekiwanych błędów")
    @ApiResponse(responseCode = "500", description = "Internal Server Error",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, WebRequest request) {
        ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Operation(summary = "Obsługa błędów walidacji parametrów")
    @ApiResponse(responseCode = "400", description = "Validation Failed",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String errors = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining("; "));

        ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            "Validation Failed",
            errors,
            request.getDescription(false)
        );
        return new ResponseEntity<>(body, headers, status);
    }

    @Operation(summary = "Obsługa błędnego JSON-a")
    @ApiResponse(responseCode = "400", description = "Malformed JSON",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String cause = ex.getMostSpecificCause().getMessage();
        ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            "Malformed JSON",
            cause,
            request.getDescription(false)
        );
        return new ResponseEntity<>(body, headers, status);
    }

    @Operation(summary = "Obsługa brakującego parametru żądania")
    @ApiResponse(responseCode = "400", description = "Missing Parameter",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String msg = String.format("Parameter '%s' is missing", ex.getParameterName());
        ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            "Missing Parameter",
            msg,
            request.getDescription(false)
        );
        return new ResponseEntity<>(body, headers, status);
    }

    @Operation(summary = "Obsługa nieobsługiwanej metody HTTP")
    @ApiResponse(responseCode = "405", description = "Method Not Allowed",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String msg = String.format("Method '%s' not supported for this endpoint", ex.getMethod());
        ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            "Method Not Allowed",
            msg,
            request.getDescription(false)
        );
        return new ResponseEntity<>(body, headers, status);
    }

    @Operation(summary = "Obsługa nieobsługiwanego typu mediów")
    @ApiResponse(responseCode = "415", description = "Unsupported Media Type",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String msg = String.format("Media type '%s' is not supported", ex.getContentType());
        ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            "Unsupported Media Type",
            msg,
            request.getDescription(false)
        );
        return new ResponseEntity<>(body, headers, status);
    }

    @Operation(summary = "Obsługa nieprawidłowego typu parametru")
    @ApiResponse(responseCode = "400", description = "Bad Request",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {

        String msg = String.format("Parameter '%s' should be of type '%s'",
            ex.getName(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );
        ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            msg,
            request.getDescription(false)
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Obsługa ResponseStatusException")
    @ApiResponse(responseCode = "default", description = "Zwraca kod i powód z ResponseStatusException",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(
            ResponseStatusException ex,
            WebRequest request) {

        int code = ex.getStatusCode().value();
        HttpStatus status = HttpStatus.resolve(code);

        ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            code,
            status != null ? status.getReasonPhrase() : "Unknown Status",
            ex.getReason(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    @Operation(summary = "Obsługa naruszeń integralności danych")
    @ApiResponse(responseCode = "409", description = "Conflict",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            WebRequest request) {

        Throwable root = ex.getMostSpecificCause();
        String userMessage = "The operation failed due to a data conflict.";

        if (root != null && root.getMessage() != null
            && root.getMessage().toLowerCase().contains("constraint")) {
            userMessage = "The email address you provided is already in use";
        }

        ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            userMessage,
            request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @Operation(summary = "Obsługa błędów walidacji (ConstraintViolation)")
    @ApiResponse(responseCode = "400", description = "Validation Failed",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request) {

        String errors = ex.getConstraintViolations().stream()
            .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
            .collect(Collectors.joining("; "));
        ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            errors,
            request.getDescription(false)
        );
        return ResponseEntity.badRequest().body(body);
    }
}
