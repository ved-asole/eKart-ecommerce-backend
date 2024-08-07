package com.vedasole.ekartecommercebackend.entity;

import com.vedasole.ekartecommercebackend.utility.AppConstant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Validated
@Entity
@Table(name = "order")
public class Order {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", allocationSize = 0)
    private long orderId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @ManyToOne(optional = false)
    @JoinColumn(name = "address_id")
    private Address address;

    @Min(value = 0, message = "Total must be greater than or equal to 0")
    private double total;

    @NotNull(message = "Order status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 30, nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "create_dt", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "update_dt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Order(long orderId, Customer customer, List<OrderItem> orderItems, Address address, double total, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.customer = customer;
        this.orderItems = orderItems;
        this.address = address;
        this.total = total;
        this.orderStatus = orderStatus;
    }

    public Order(Customer customer, List<OrderItem> orderItems, Address address, double total, OrderStatus orderStatus) {
        this.customer = customer;
        this.orderItems = orderItems;
        this.address = address;
        this.total = total;
        this.orderStatus = orderStatus;
    }

    @PrePersist
    @PreUpdate
    private void onPersistOrUpdate() {
        calculateTotal();
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        if (this.orderItems != null) {
            this.orderItems.forEach(orderItem -> orderItem.setOrder(null));
        }
        this.orderItems = orderItems;
        if (orderItems != null) {
            this.orderItems.forEach(orderItem -> orderItem.setOrder(this));
        }
        calculateTotal();
    }

    public void calculateTotal() {
        setTotal(0);
        if(!(orderItems == null || orderItems.isEmpty())) {
            orderItems.forEach(orderItem -> {
                double totalProductValue = orderItem.getProduct().getPrice() * orderItem.getQuantity();
                setTotal(getTotal() + totalProductValue);
            });
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customer=" + customer +
                ", address=" + address +
                ", total=" + total +
                ", orderStatus=" + orderStatus +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}