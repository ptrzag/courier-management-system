package pl.polsl.courier.management.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.courier.management.system.entity.Client;
import pl.polsl.courier.management.system.entity.Parcel;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ClientDTO", description = "DTO dla encji Client")
public class ClientDTO {
    @Schema(description = "Unikalne ID klienta", example = "42")
    private Long id;

    @NotBlank(message = "firstName must not be blank")
    @Schema(description = "Imię klienta", example = "Jan")
    private String firstName;

    @NotBlank(message = "lastName must not be blank")
    @Schema(description = "Nazwisko klienta", example = "Kowalski")
    private String lastName;

    @Email(message = "must be a well-formed email address")
    @NotBlank(message = "email must not be blank")
    @Schema(description = "Email klienta", example = "jan.kowalski@example.com")
    private String email;

    @NotBlank(message = "phoneNumber must not be blank")
    @Pattern(regexp = "\\+?[0-9\\-]+", message = "invalid phone number")
    @Schema(description = "Numer telefonu", example = "+48123123123")
    private String phoneNumber;

    @NotBlank(message = "address must not be blank")
    @Schema(description = "Adres klienta", example = "ul. Przykładowa 10, 00-950 Warszawa")
    private String address;

    @Schema(description = "Lista ID przesyłek klienta", example = "[100, 101]")
    private List<Long> parcelIds;

    public ClientDTO(Client c) {
        this.id = c.getId();
        this.firstName = c.getFirstName();
        this.lastName = c.getLastName();
        this.email = c.getEmail();
        this.phoneNumber = c.getPhoneNumber();
        this.address = c.getAddress();
        this.parcelIds = c.getParcels().stream()
            .map(Parcel::getId)
            .collect(Collectors.toList());
    }
}
