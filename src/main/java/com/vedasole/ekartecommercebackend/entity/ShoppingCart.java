package com.vedasole.ekartecommercebackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    @JsonIgnore
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shoppingCart", fetch = FetchType.LAZY)
    private List<ShoppingCartItem> shoppingCartItems;

    @Column(name = "total", nullable = false)
    @Min(value = 0, message = "Total must be greater than or equal to 0")
    private double total;

    @Column(name = "discount")
    @Min(value = 0, message = "Discount must not be negative")
    private double discount;

    @Column(name = "create_dt", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "update_dt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void onPersistOrUpdate() {
        calculateTotalAndDiscount();
    }

    public void setShoppingCartItems(List<ShoppingCartItem> shoppingCartItems) {
        if (this.shoppingCartItems != null) {
            this.shoppingCartItems.forEach(item -> item.setShoppingCart(null));
        }
        this.shoppingCartItems = shoppingCartItems;
        if (shoppingCartItems != null) {
            this.shoppingCartItems.forEach(item -> item.setShoppingCart(this));
        }
        calculateTotalAndDiscount();
    }

    public void calculateTotalAndDiscount() {
        setTotal(0);
        setDiscount(0);
        if(!(shoppingCartItems == null || shoppingCartItems.isEmpty())) {
            shoppingCartItems.forEach(shoppingCartItem -> {
                double totalProductValue = shoppingCartItem.getProduct().getPrice() * shoppingCartItem.getQuantity();
                setTotal(getTotal() + totalProductValue);
                setDiscount(getDiscount() + totalProductValue * shoppingCartItem.getProduct().getDiscount() / 100);
            });
        }
    }

    @Override
    public String toString() {
        return "ShoppingCart{" +
                "cartId=" + cartId +
                ", customer=" + customer.getCustomerId() +
                ", shoppingCartItems=" + shoppingCartItems +
                ", total=" + total +
                ", discount=" + discount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
