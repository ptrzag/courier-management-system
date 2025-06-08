package pl.polsl.courier.management.system.dto;

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
public class ClientDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;

    // nowe pole
    private List<Long> parcelIds;

    public ClientDTO(Client c) {
        this.id = c.getId();
        this.firstName = c.getFirstName();
        this.lastName = c.getLastName();
        this.email = c.getEmail();
        this.phoneNumber = c.getPhoneNumber();
        this.address = c.getAddress();
        // wypakowujemy ID wszystkich paczek
        this.parcelIds = c.getParcels().stream()
            .map(Parcel::getId)
            .collect(Collectors.toList());
    }
}
