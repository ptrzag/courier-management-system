package pl.polsl.courier.management.system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.courier.management.system.entity.Car;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDTO {
    private Long id;
    @NotBlank(message = "brand must not be blank")
    private String brand;

    @NotBlank(message = "model must not be blank")
    private String model;

    @NotBlank(message = "registrationNumber must not be blank")
    private String registrationNumber;

    @Min(value = 0, message = "mileage must be at least 0")
    private int mileage;

    @Positive(message = "capacity must be greater than 0")
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
