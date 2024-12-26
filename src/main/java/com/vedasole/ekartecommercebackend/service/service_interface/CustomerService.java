package com.vedasole.ekartecommercebackend.service.service_interface;

import com.vedasole.ekartecommercebackend.entity.Customer;
import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.payload.CustomerDto;
import com.vedasole.ekartecommercebackend.payload.NewCustomerDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {

    CustomerDto createCustomer(NewCustomerDto newCustomerDto);
    CustomerDto updateCustomer(CustomerDto customerDto , Long customerId);
    List<CustomerDto> getAllCustomers();
    Page<CustomerDto> getAllCustomersByPage(int page, int size, String sortBy, String sortOrder);
    CustomerDto getCustomerById(Long customerId);
    CustomerDto getCustomerByEmail(String email);
    void deleteCustomer(Long customerId);
    User getUserForCustomer(Long customerId);
    Long getTotalCustomersCount();
    Customer convertToCustomer(CustomerDto customerDto);
    CustomerDto convertToCustomerDto(Customer customer);

}