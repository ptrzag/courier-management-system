package pl.polsl.courier.management.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.courier.management.system.entity.Parcel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcelDTO {
    private Long id;
    private String contentDescription;
    private String senderAddress;
    private String recipientAddress;
    private LocalDateTime dispatchDate;
    private LocalDateTime deliveryDate;
    private BigDecimal weight;
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
