package com.ecommerce.project.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @NotBlank
    @Size(min = 5,message = "Street name must be at least 5 letter")
    private String street;

    @NotBlank
    @Size(min = 5,message = "Building name must be at least 5 letter")
    @Column(name = "building_name")
    private String building;

    @NotBlank
    @Size(min = 3,message = "City name must be at least 3 letter")
    private String city;

    @NotBlank
    @Size(min = 2,message = "State name must be at least 2 letter")
    private String state;

    @NotBlank
    @Size(min = 2,message = "Country name must be at least 2 letter")
    private String country;

    @NotBlank
    @Size(min = 6,max = 6, message = "Pin code name must be  6 letter")
    private String zipCode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users= new ArrayList<>();

    public Address(String street, String building, String city, String state, String country, String zipCode) {
        this.street = street;
        this.building = building;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
    }
}
