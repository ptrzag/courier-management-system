package pl.polsl.courier.management.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.courier.management.system.entity.RoutePlan;
import pl.polsl.courier.management.system.entity.Parcel;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutePlanDTO {
    private Long id;
    private String startLocation;
    private String endLocation;
    private Double distance;
    private Integer estimatedTime;
    private LocalDate scheduleDate;
    private List<String> stops;
    private Long carId;
    private List<Long> parcelIds;   // ← nowe pole

    public RoutePlanDTO(RoutePlan r) {
        this.id = r.getId();
        this.startLocation = r.getStartLocation();
        this.endLocation = r.getEndLocation();
        this.distance = r.getDistance();
        this.estimatedTime = r.getEstimatedTime();
        this.scheduleDate = r.getScheduleDate();
        this.stops = r.getStops();
        this.carId = r.getCar() != null ? r.getCar().getId() : null;
        // wypakowujemy ID wszystkich paczek
        this.parcelIds = r.getParcel().stream()
                         .map(Parcel::getId)
                         .collect(Collectors.toList());
    }
}
