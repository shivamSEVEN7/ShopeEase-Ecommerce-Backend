package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ShippingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shippingAddressId;
    @NotBlank
    @Size(max = 30, message = "Name can't be longer than 30 characters" )
    private String name;

    @Size(min = 3, message = "Building Name must be at least 5 characters long" )
    private String buildingName;
    @NotBlank
    @Size(min = 3, message = "City Name must be at least 5 characters long" )
    private String city;
    @NotBlank
    @Size(min = 3, message = "State Name must be at least 5 characters long" )
    private String state;
    @NotBlank
    @Size(min = 3, message = "locality must be at least 5 characters long" )
    private String locality;
    @NotBlank
    @Size(min = 6, message = "Zipcode must be at least 6 characters" )
    private String zipcode;
    @NotBlank
    private String mobileNumber;
    private String landmark;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
