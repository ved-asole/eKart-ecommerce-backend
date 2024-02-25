package com.vedasole.ekartecommercebackend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * Entity class for Category
 */
@Builder
@Validated
@Entity
@Data
@Table(name = "category")
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = -5392075886775352349L;

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", allocationSize = 0)
    private long categoryId;

    @Column(nullable = false, length = 20)
    private String name;

    private String image;

    @Column(length = 1000)
    private String desc;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @JsonProperty
    private boolean active;

    public Category() {}

    public Category(String name, String image, String desc, Category parentCategory, boolean active) {
        this.name = name;
        this.image = image;
        this.desc = desc;
        this.parentCategory = parentCategory;
        this.active = active;
    }

    public Category(long categoryId, String name, String image, String desc, Category parentCategory, boolean active) {
        this.categoryId = categoryId;
        this.name = name;
        this.image = image;
        this.desc = desc;
        this.parentCategory = parentCategory;
        this.active = active;
    }
//
//    public long getCategoryId() {
//        return categoryId;
//    }
//
//    public void setCategoryId(long categoryId) {
//        this.categoryId = categoryId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }
//
//    public String getDesc() {
//        return desc;
//    }
//
//    public void setDesc(String desc) {
//        this.desc = desc;
//    }
//
//    public Category getParentCategory() {
//        return parentCategory;
//    }
//
//    public void setParentCategory(Category parentCategory) {
//        this.parentCategory = parentCategory;
//    }
//
//    @Override
//    public final boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null) return false;
//        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
//        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
//        if (thisEffectiveClass != oEffectiveClass) return false;
//        Category category = (Category) o;
//        return Long.valueOf(this.getCategoryId()) != null && Objects.equals(getCategoryId(), category.getCategoryId());
//    }
//
//    @Override
//    public final int hashCode() {
//        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
//    }
}
