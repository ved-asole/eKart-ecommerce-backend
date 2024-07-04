package com.vedasole.ekartecommercebackend.entity;

import com.vedasole.ekartecommercebackend.utility.AppConstant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Validated
@Entity
@Table(name = "shop_order")
public class Order {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_order_seq")
    @SequenceGenerator(name = "shop_order_seq", allocationSize = 0)
    private long orderId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;

    @ManyToOne(optional = false)
    @JoinColumn(name = "address_id")
    private Address address;

    @Min(value = 0, message = "Total must be greater than or equal to 0")
    private double total;

    @NotNull(message = "Order status is required")
    @NotBlank(message = "Order status should not be blank")
    private OrderStatus orderStatus;

    @CreatedDate
    private LocalDateTime createDt;

    @UpdateTimestamp
    private LocalDateTime updateDt;


    @PrePersist
    private void onCreate() {
        createDt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        updateDt = LocalDateTime.now();
    }

}

