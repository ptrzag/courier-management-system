package pl.polsl.courier.management.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.courier.management.system.entity.Parcel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcelDTO {
    private Long id;
    @NotBlank(message = "contentDescription must not be blank")
    private String contentDescription;

    @NotBlank(message = "senderAddress must not be blank")
    private String senderAddress;

    @NotBlank(message = "recipientAddress must not be blank")
    private String recipientAddress;

    @NotNull(message = "dispatchDate must not be null")
    private LocalDateTime dispatchDate;

    @NotNull(message = "deliveryDate must not be null")
    private LocalDateTime deliveryDate;

    @NotNull(message = "weight must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "weight must be at least 0")
    private BigDecimal weight;

    @NotNull(message = "price must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "price must be at least 0")
    private BigDecimal price;
    
    private Long clientId;
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
