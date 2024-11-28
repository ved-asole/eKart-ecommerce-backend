package com.vedasole.ekartecommercebackend.service.serviceInterface;

import com.vedasole.ekartecommercebackend.entity.Customer;
import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.payload.CustomerDto;

import java.util.List;

public interface CustomerService {

    CustomerDto createCustomer(CustomerDto customerDto);
    CustomerDto updateCustomer(CustomerDto customerDto , Long customerId);
    List<CustomerDto> getAllCustomers();
    CustomerDto getCustomerById(Long customerId);
    CustomerDto getCustomerByEmail(String email);
    void deleteCustomer(Long customerId);
    User getUserForCustomer(Long customerId);
    Customer convertToCustomer(CustomerDto customerDto);
    CustomerDto convertToCustomerDto(Customer customer);

}