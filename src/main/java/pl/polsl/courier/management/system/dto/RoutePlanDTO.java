package pl.polsl.courier.management.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.courier.management.system.entity.Parcel;
import pl.polsl.courier.management.system.entity.RoutePlan;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RoutePlanDTO", description = "DTO dla encji RoutePlan")
public class RoutePlanDTO {
    @Schema(description = "Unikalne ID planu trasy", example = "7")
    private Long id;

    @NotBlank(message = "startLocation must not be blank")
    @Pattern(regexp = "^[^,]+,\\s*\\d{2}-\\d{3}\\s+.+$", message = "Format adresu: 'ul. Przykładowa 10, 00-950 Warszawa'")
    @Schema(description = "Punkt startowy", example = "ul. Startowa 1, 00-001 Warszawa")
    private String startLocation;

    @NotBlank(message = "endLocation must not be blank")
    @Pattern(regexp = "^[^,]+,\\s*\\d{2}-\\d{3}\\s+.+$", message = "Format adresu: 'ul. Przykładowa 10, 00-950 Warszawa'")
    @Schema(description = "Punkt końcowy", example = "ul. Końcowa 2, 00-002 Warszawa")
    private String endLocation;

    @NotNull(message = "distance must not be null")
    @PositiveOrZero(message = "distance must be at least 0")
    @Schema(description = "Długość trasy (km)", example = "15.4")
    private Double distance;

    @NotNull(message = "estimatedTime must not be null")
    @PositiveOrZero(message = "estimatedTime must be at least 0")
    @Schema(description = "Szacowany czas (min)", example = "45")
    private Integer estimatedTime;

    @NotNull(message = "scheduleDate must not be null")
    @Schema(description = "Data realizacji", example = "2025-06-17")
    private LocalDate scheduleDate;

    @NotNull(message = "stops must not be null")
    @Size(min = 1, message = "stops list must contain at least one element")
    @Schema(description = "Lista przystanków", example = "[\"ul. A 1, 00-001 Warszawa\"]")
    private List<
      @Pattern(regexp = "^[^,]+,\\s*\\d{2}-\\d{3}\\s+.+$", message = "Format przystanku")
      String
    > stops;

    @Schema(description = "ID pojazdu", example = "1")
    private Long carId;

    @Schema(description = "Lista ID przesyłek", example = "[100, 101]")
    private List<
      @NotNull(message = "parcelId must not be null")
      Long
    > parcelIds;

    public RoutePlanDTO(RoutePlan r) {
        this.id = r.getId();
        this.startLocation = r.getStartLocation();
        this.endLocation = r.getEndLocation();
        this.distance = r.getDistance();
        this.estimatedTime = r.getEstimatedTime();
        this.scheduleDate = r.getScheduleDate();
        this.stops = r.getStops();
        this.carId = r.getCar() != null ? r.getCar().getId() : null;
        this.parcelIds = r.getParcel().stream()
                         .map(Parcel::getId)
                         .collect(Collectors.toList());
    }
}
