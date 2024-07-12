package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> { }