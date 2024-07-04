package com.vedasole.ekartecommercebackend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Validated
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", allocationSize = 0)
    private long customerId;

    @NotNull
    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @NotBlank
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull
    @NotBlank
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "create_dt")
    @CreatedDate
    private LocalDateTime createDt;

    @Column(name = "update_dt")
    @UpdateTimestamp
    private LocalDateTime updateDt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShoppingCart shoppingCart;

    @PrePersist
    private void onCreate() {
        createDt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        updateDt = LocalDateTime.now();
    }

}
