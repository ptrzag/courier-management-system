package pl.polsl.courier.management.system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.courier.management.system.entity.Car;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CarDTO", description = "DTO dla encji Car")
public class CarDTO {
    @Schema(description = "Unikalne ID pojazdu", example = "1")
    private Long id;

    @NotBlank(message = "brand must not be blank")
    @Schema(description = "Marka pojazdu", example = "Ford")
    private String brand;

    @NotBlank(message = "model must not be blank")
    @Schema(description = "Model pojazdu", example = "Transit")
    private String model;

    @NotBlank(message = "registrationNumber must not be blank")
    @Schema(description = "Numer rejestracyjny", example = "WX12345")
    private String registrationNumber;

    @Min(value = 0, message = "mileage must be at least 0")
    @Schema(description = "Przebieg (km)", example = "120000")
    private int mileage;

    @Positive(message = "capacity must be greater than 0")
    @Schema(description = "Pojemność (t)", example = "1.5")
    private double capacity;

    public CarDTO(Car car) {
        this.id = car.getId();
        this.brand = car.getBrand();
        this.model = car.getModel();
        this.registrationNumber = car.getRegistrationNumber();
        this.mileage = car.getMileage();
        this.capacity = car.getCapacity();
    }
}
