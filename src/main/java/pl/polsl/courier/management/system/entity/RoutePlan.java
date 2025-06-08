package pl.polsl.courier.management.system.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RoutePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Wymuszony format: "<dowolny tekst bez przecinka>, <2 cyfry>-<3 cyfry> <dowolny tekst>"
    @Pattern(
      regexp = "^[^,]+,\\s*\\d{2}-\\d{3}\\s+.+$",
      message = "Format adresu: 'ul. Przykładowa 10, 00-950 Warszawa'"
    )
    private String startLocation;

    @Pattern(
      regexp = "^[^,]+,\\s*\\d{2}-\\d{3}\\s+.+$",
      message = "Format adresu: 'ul. Przykładowa 10, 00-950 Warszawa'"
    )
    private String endLocation;

    private Double distance;
    private Integer estimatedTime;

    @Column(name = "scheduled_date")
    private LocalDate scheduleDate;

    @ElementCollection
    @CollectionTable(
        name = "route_stops",
        joinColumns = @JoinColumn(name = "route_plan_id")
    )
    @Column(name = "stop_address")
    private List<
      @Pattern(
        regexp = "^[^,]+,\\s*\\d{2}-\\d{3}\\s+.+$",
        message = "Format przystanku: 'ul. Przykładowa 10, 00-950 Warszawa'"
      )
      String
    > stops;

    //@JsonIgnore - mozliwe ze konieczny, ale inaczej nie dziala update
    @OneToMany(mappedBy = "routePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Parcel> parcel = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
}
