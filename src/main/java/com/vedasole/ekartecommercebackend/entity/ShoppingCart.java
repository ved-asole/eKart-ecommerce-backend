package com.vedasole.ekartecommercebackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Validated
@Entity
@Table(name = "cart")
public class ShoppingCart {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_seq")
    @SequenceGenerator(name = "cart_seq", allocationSize = 0)
    private long cartId;

    @OneToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shoppingCart")
    private List<ShoppingCartItem> shoppingCartItems;

    @Column(name = "total", nullable = false)
    @Min(value = 0, message = "Total must be greater than or equal to 0")
    private double total;

    @Column(name = "discount")
    @Min(value = 0, message = "Discount must not be negative")
    private double discount;

    @Column(name = "create_dt", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "update_dt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void setShoppingCartItems(List<ShoppingCartItem> shoppingCartItems) {
        if(!(shoppingCartItems == null || shoppingCartItems.isEmpty())) {
            this.shoppingCartItems = shoppingCartItems;
            shoppingCartItems.forEach(shoppingCartItem -> {
                this.total += shoppingCartItem.getProduct().getPrice() * shoppingCartItem.getQuantity();
                this.discount += shoppingCartItem.getProduct().getDiscount();
            });
        }
    }

    public void setTotal(double total) {
        this.total = total;
        calculateTotalAndDiscount();
    }

    public void setDiscount(double discount) {
        this.discount = discount;
        calculateTotalAndDiscount();
    }

    public void calculateTotalAndDiscount() {
        this.total = 0;
        this.discount = 0;
        if(!(shoppingCartItems == null || shoppingCartItems.isEmpty())) {
            shoppingCartItems.forEach(shoppingCartItem -> {
                this.total += shoppingCartItem.getProduct().getPrice() * shoppingCartItem.getQuantity();
                this.discount += shoppingCartItem.getProduct().getDiscount();
            });
        }
    }

    @Override
    public String toString() {
        return "ShoppingCart{" +
                "cartId=" + cartId +
                ", customer=" + customer +
                ", shoppingCartItems=" + shoppingCartItems +
                ", total=" + total +
                ", discount=" + discount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
