package pl.polsl.courier.management.system.entity;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Getter
@Setter
@Schema(name = "Car", description = "Pojazd przewożący przesyłki")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unikalny identyfikator pojazdu", example = "1")
    private Long id;

    @Schema(description = "Marka samochodu", example = "Ford")
    private String brand;

    @Schema(description = "Model samochodu", example = "Transit")
    private String model;

    @Schema(description = "Numer rejestracyjny", example = "WX12345")
    private String registrationNumber;

    @Schema(description = "Przebieg w kilometrach", example = "120000")
    private int mileage;

    @Schema(description = "Pojemność ładunkowa (w tonach)", example = "1.5")
    private double capacity;

    @JsonIgnore
    @OneToMany(mappedBy = "car")
    private List<RoutePlan> routes;
}
