package com.vedasole.ekartecommercebackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Validated
@Entity
@Table(name = "address")
public class Address {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", allocationSize = 0)
    private long addressId;

    @Column(name = "add_line1", length = 100, nullable = false)
    private String addLine1;

    @Column(name = "add_line2", length = 100)
    private String addLine2;


    @Column(name = "city", length = 50, nullable = false)
    private String city;


    @Column(name = "state", length = 50, nullable = false)
    private String state;

    @Column(name = "country", length = 50, nullable = false)
    private String country;

    @Column(name = "postal_code", length = 10, nullable = false)
    private int postalCode;

    @Column(name = "create_dt", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "update_dt", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Address(long addressId, String addLine1, String addLine2, String city, String state, String country, int postalCode) {
        this.addressId = addressId;
        this.addLine1 = addLine1;
        this.addLine2 = addLine2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }

    public Address(String addLine1, String addLine2, String city, String state, String country, int postalCode) {
        this.addLine1 = addLine1;
        this.addLine2 = addLine2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }
}