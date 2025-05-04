package pl.polsl.courier.management.system.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Car {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String brand;
	private String model;
	private String registrationNumber;
	private int mileage;
	private double capacity;
	
	@JsonIgnore
	@OneToMany(mappedBy = "car")
	private List<RoutePlan> routes;
}