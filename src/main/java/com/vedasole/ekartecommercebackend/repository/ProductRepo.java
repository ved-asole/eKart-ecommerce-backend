package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {

    List<Product> findByNameIsContainingIgnoreCaseOrDescContainingIgnoreCase(String name, String desc, Pageable pageable);

    List<Product> findByCategoryCategoryId(long categoryId);

    Page<Product> findByCategoryCategoryId(long categoryId, Pageable pageable);

}