// ErrorResponse.java
package pl.polsl.courier.management.system.exception;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "ErrorResponse", description = "Body odpowiedzi w przypadku błędu")
public class ErrorResponse {

    @Schema(description = "Czas wystąpienia błędu", example = "2025-06-17T12:45:30")
    private LocalDateTime timestamp;

    @Schema(description = "Kod statusu HTTP", example = "400")
    private int status;

    @Schema(description = "Krótki opis błędu", example = "Bad Request")
    private String error;

    @Schema(description = "Szczegółowa wiadomość", example = "Validation failed for field 'name'")
    private String message;

    @Schema(description = "Ścieżka żądania", example = "/api/clients")
    private String path;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
