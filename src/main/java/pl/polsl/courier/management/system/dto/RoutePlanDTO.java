package pl.polsl.courier.management.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.courier.management.system.entity.RoutePlan;
import pl.polsl.courier.management.system.entity.Parcel;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutePlanDTO {
    private Long id;
    @NotBlank(message = "startLocation must not be blank")
    @Pattern(
      regexp = "^[^,]+,\\s*\\d{2}-\\d{3}\\s+.+$",
      message = "Format adresu: 'ul. Przykładowa 10, 00-950 Warszawa'"
    )
    private String startLocation;

    @NotBlank(message = "endLocation must not be blank")
    @Pattern(
      regexp = "^[^,]+,\\s*\\d{2}-\\d{3}\\s+.+$",
      message = "Format adresu: 'ul. Przykładowa 10, 00-950 Warszawa'"
    )
    private String endLocation;

    @NotNull(message = "distance must not be null")
    @PositiveOrZero(message = "distance must be at least 0")
    private Double distance;

    @NotNull(message = "estimatedTime must not be null")
    @PositiveOrZero(message = "estimatedTime must be at least 0")
    private Integer estimatedTime;

    @NotNull(message = "scheduleDate must not be null")
    private LocalDate scheduleDate;

    @NotNull(message = "stops must not be null")
    @Size(min = 1, message = "stops list must contain at least one element")
    private List<
      @Pattern(
        regexp = "^[^,]+,\\s*\\d{2}-\\d{3}\\s+.+$",
        message = "Format przystanku: 'ul. Przykładowa 10, 00-950 Warszawa'"
      )
      String
    > stops;

    private Long carId;

    private List<@NotNull(message = "parcelId must not be null") Long> parcelIds;

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
