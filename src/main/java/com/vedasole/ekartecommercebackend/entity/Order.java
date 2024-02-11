package com.vedasole.ekartecommercebackend.entity;

import com.vedasole.ekartecommercebackend.utility.OrderStatus;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

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

    @OneToMany(mappedBy = "order")
//    @JoinColumn(name = "order_details_id")
    private List<OrderDetail> orderDetails;

    @ManyToOne(optional = false)
    @JoinColumn(name = "address_id")
    private Address address;

    private LocalDateTime orderDt;

    private double total;

    private OrderStatus orderStatus;

}

