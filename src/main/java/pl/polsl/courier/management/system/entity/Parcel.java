package pl.polsl.courier.management.system.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Parcel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	String contentDescription;
	String senderAddress; 
	String recipientAddress; 
	LocalDateTime dispatchDate;
	LocalDateTime deliveryDate; 
	//PackageStatus status; // for example CREATED, SENT, IN_TRANSIT, DELIVERED, LOST
	BigDecimal weight; 
	BigDecimal price; 
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "client_id")
	private Client client;
}
