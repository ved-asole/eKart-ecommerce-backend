package com.vedasole.ekartecommercebackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Validated
@Entity
@Table(name = "cart_item")
public class ShoppingCartItem {

    @Id
    @Column(name = "cart_item_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_item_seq")
    @SequenceGenerator(name = "cart_item_seq", allocationSize = 0)
    private long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart shoppingCart;

    @Column(name = "quantity", nullable = false)
    @Min(value = 0, message = "Product quantity cannot be negative")
    private long quantity;

    @Column(name = "create_dt", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "update_dt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateShoppingCartTotal() {
        this.getShoppingCart().calculateTotalAndDiscount();
    }

    @Override
    public String toString() {
        return "ShoppingCartItem{" +
                "cartItemId=" + cartItemId +
                ", product=" + product +
                ", quantity=" + quantity +
                '}';
    }

}
