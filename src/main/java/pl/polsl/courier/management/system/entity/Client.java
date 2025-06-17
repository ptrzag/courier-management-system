package pl.polsl.courier.management.system.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Getter
@Setter
@Table(
  name = "client",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = "email"),
    @UniqueConstraint(columnNames = "phone_number")
  }
)
@Schema(name = "Client", description = "Dane klienta korzystającego z usług kurierskich")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unikalny identyfikator klienta", example = "42")
    private Long id;

    @Schema(description = "Imię klienta", example = "Jan")
    private String firstName;

    @Schema(description = "Nazwisko klienta", example = "Kowalski")
    private String lastName;

    @Column(nullable = false, unique = true)
    @Schema(description = "Email klienta", example = "jan.kowalski@example.com")
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    @Schema(description = "Numer telefonu", example = "+48123123123")
    private String phoneNumber;

    @Schema(description = "Adres klienta", example = "ul. Przykładowa 10, 00-950 Warszawa")
    private String address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Parcel> parcels = new ArrayList<>();
}
