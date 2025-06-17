package pl.polsl.courier.management.system.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Getter
@Setter
@Schema(name = "Parcel", description = "Przesyłka transportowana przez system")
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unikalny identyfikator przesyłki", example = "100")
    private Long id;

    @Schema(description = "Opis zawartości przesyłki", example = "Dokumenty")
    private String contentDescription;

    @Schema(description = "Adres nadawcy", example = "ul. Nadawcza 5, 00-001 Warszawa")
    private String senderAddress;

    @Schema(description = "Adres odbiorcy", example = "ul. Odbiorcza 8, 00-002 Warszawa")
    private String recipientAddress;

    @Schema(description = "Data i godzina nadania", example = "2025-06-15T09:30:00")
    private LocalDateTime dispatchDate;

    @Schema(description = "Data i godzina dostawy", example = "2025-06-16T14:45:00")
    private LocalDateTime deliveryDate;

    @Schema(description = "Waga przesyłki (kg)", example = "2.5")
    private BigDecimal weight;

    @Schema(description = "Cena usługi (PLN)", example = "50.00")
    private BigDecimal price;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "route_plan_id")
    private RoutePlan routePlan;
}
