package com.vedasole.ekartecommercebackend.entity;


import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
    private String firstName;
    private String lastName;
    private long phoneNumber;
    private String email;
    private String password;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;
}
