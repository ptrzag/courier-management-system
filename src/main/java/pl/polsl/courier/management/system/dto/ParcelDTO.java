package pl.polsl.courier.management.system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.courier.management.system.entity.Parcel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ParcelDTO", description = "DTO dla encji Parcel")
public class ParcelDTO {
    @Schema(description = "Unikalne ID przesyłki", example = "100")
    private Long id;

    @NotBlank(message = "contentDescription must not be blank")
    @Schema(description = "Opis zawartości", example = "Dokumenty")
    private String contentDescription;

    @NotBlank(message = "senderAddress must not be blank")
    @Schema(description = "Adres nadawcy", example = "ul. Nadawcza 5, 00-001 Warszawa")
    private String senderAddress;

    @NotBlank(message = "recipientAddress must not be blank")
    @Schema(description = "Adres odbiorcy", example = "ul. Odbiorcza 8, 00-002 Warszawa")
    private String recipientAddress;

    @NotNull(message = "dispatchDate must not be null")
    @Schema(description = "Data i godzina nadania", example = "2025-06-15T09:30:00")
    private LocalDateTime dispatchDate;

    @NotNull(message = "deliveryDate must not be null")
    @Schema(description = "Data i godzina dostawy", example = "2025-06-16T14:45:00")
    private LocalDateTime deliveryDate;

    @NotNull(message = "weight must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "weight must be at least 0")
    @Schema(description = "Waga (kg)", example = "2.5")
    private BigDecimal weight;

    @NotNull(message = "price must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "price must be at least 0")
    @Schema(description = "Cena (PLN)", example = "50.00")
    private BigDecimal price;

    @Schema(description = "ID klienta", example = "42")
    private Long clientId;

    @Schema(description = "ID planu trasy", example = "7")
    private Long routePlanId;

    public ParcelDTO(Parcel p) {
        this.id = p.getId();
        this.contentDescription = p.getContentDescription();
        this.senderAddress = p.getSenderAddress();
        this.recipientAddress = p.getRecipientAddress();
        this.dispatchDate = p.getDispatchDate();
        this.deliveryDate = p.getDeliveryDate();
        this.weight = p.getWeight();
        this.price = p.getPrice();
        this.clientId = p.getClient() != null ? p.getClient().getId() : null;
        this.routePlanId = p.getRoutePlan() != null ? p.getRoutePlan().getId() : null;
    }
}
