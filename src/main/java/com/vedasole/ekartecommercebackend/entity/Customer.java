package com.vedasole.ekartecommercebackend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @SequenceGenerator(name = "customer_seq", allocationSize = 1)
    private long customerId;

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name cannot be blank")@Size(
            min = 3,
            max = 20,
            message = "First name must be between minimum of 3 characters " +
                    "and maximum of 20 characters"
    )
    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @NotNull(message = "Last name is required")
    @NotBlank(message = "Last name cannot be blank")
    @Size(
            min = 3,
            max = 20,
            message = "Last name must be between minimum of 3 characters " +
                    "and maximum of 20 characters"
    )
    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @NotNull(message = "Phone number is required")
    @NotBlank(message = "Phone number cannot be blank")
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull(message = "User is required")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", unique = true, nullable = false, updatable = false)
    private User user;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private ShoppingCart shoppingCart;

    @Column(name = "create_dt", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "update_dt", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}