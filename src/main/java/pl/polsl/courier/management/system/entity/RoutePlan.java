package pl.polsl.courier.management.system.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RoutePlan {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String startLocation;
	private String endLocation;
	private Double distance;
	private Integer estimatedTime;
	@Column(name = "scheduled_date")
	private LocalDate scheduleDate;
	
	@JsonIgnore
	@OneToMany(mappedBy = "routePlan", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Parcel> parcel;
	
	@ManyToOne
	@JoinColumn(name = "car_id")
	private Car car;
}
