package pl.polsl.courier.management.system.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Getter
@Setter
@Schema(name = "RoutePlan", description = "Plan trasy realizacji przesyłek")
public class RoutePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unikalny identyfikator planu trasy", example = "7")
    private Long id;

    @Pattern(
      regexp = "^[^,]+,\\s*\\d{2}-\\d{3}\\s+.+$",
      message = "Format: 'ul. Przykładowa 10, 00-950 Warszawa'"
    )
    @Schema(description = "Punkt startowy trasy", example = "ul. Startowa 1, 00-001 Warszawa")
    private String startLocation;

    @Pattern(
      regexp = "^[^,]+,\\s*\\d{2}-\\d{3}\\s+.+$",
      message = "Format: 'ul. Przykładowa 10, 00-950 Warszawa'"
    )
    @Schema(description = "Punkt końcowy trasy", example = "ul. Końcowa 2, 00-002 Warszawa")
    private String endLocation;

    @Schema(description = "Długość trasy w kilometrach", example = "15.4")
    private Double distance;

    @Schema(description = "Szacowany czas (minuty)", example = "45")
    private Integer estimatedTime;

    @Column(name = "scheduled_date")
    @Schema(description = "Data zaplanowanej realizacji", example = "2025-06-17")
    private LocalDate scheduleDate;

    @ElementCollection
    @CollectionTable(
        name = "route_stops",
        joinColumns = @JoinColumn(name = "route_plan_id")
    )
    @Column(name = "stop_address")
    @Schema(description = "Lista przystanków na trasie")
    private List<
      @Pattern(
        regexp = "^[^,]+,\\s*\\d{2}-\\d{3}\\s+.+$",
        message = "Format: 'ul. Przykładowa 10, 00-950 Warszawa'"
      )
      String
    > stops;

    @OneToMany(mappedBy = "routePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Przesyłki powiązane z tą trasą")
    private List<Parcel> parcel = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "car_id")
    @Schema(description = "Przypisany pojazd do trasy")
    private Car car;
}
