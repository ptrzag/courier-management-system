package pl.polsl.courier.management.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.courier.management.system.entity.Client;
import pl.polsl.courier.management.system.entity.Parcel;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private Long id;
    @NotBlank(message = "firstName must not be blank")
    private String firstName;

    @NotBlank(message = "lastName must not be blank")
    private String lastName;

    @Email(message = "must be a well-formed email address")
    @NotBlank(message = "email must not be blank")
    private String email;

    @NotBlank(message = "phoneNumber must not be blank")
    @Pattern(regexp = "\\+?[0-9\\-]+", message = "invalid phone number")
    private String phoneNumber;

    @NotBlank(message = "address must not be blank")
    private String address;

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
