package com.vedasole.ekartecommercebackend.service.serviceInterface;

import com.vedasole.ekartecommercebackend.entity.Customer;
import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.payload.CustomerDto;

import java.util.List;

public interface CustomerService {

    public CustomerDto createCustomer(CustomerDto customerDto);

    public CustomerDto updateCustomer(CustomerDto customerDto , Long customerId);

    public List<CustomerDto> getAllCustomers();

    public CustomerDto getCustomerById(Long customerId);

    public CustomerDto getCustomerByEmail(String email);

    void deleteCustomer(Long customerId);

    public User getUserForCustomer(Long customerId);

    public Customer convertToCustomer(CustomerDto customerDto);

}
