package com.vedasole.ekartecommercebackend.controller;

import com.vedasole.ekartecommercebackend.exception.APIException;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.ApiResponse;
import com.vedasole.ekartecommercebackend.payload.CustomerDto;
import com.vedasole.ekartecommercebackend.payload.NewCustomerDto;
import com.vedasole.ekartecommercebackend.security.JwtService;
import com.vedasole.ekartecommercebackend.service.service_interface.CustomerService;
import com.vedasole.ekartecommercebackend.service.service_interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.CUSTOMERS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class is the controller for the Customer API. It provides endpoints for creating, updating, deleting, and retrieving customers.
 */
@Validated
@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin(value = {
        "http://localhost:5173",
        "https://ekart.vedasole.me",
        "https://ekart-shopping.netlify.app",
        "https://develop--ekart-shopping.netlify.app"
}, allowCredentials = "true")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Creates a new customer.
     *
     * @param newCustomerDto the customer data to create
     * @return a response with the created customer data and links to itself and the list of customers
     */
    @PostMapping
    public ResponseEntity<EntityModel<CustomerDto>> createCustomer(
            @Valid @RequestBody NewCustomerDto newCustomerDto
    ) {
        log.debug("New Customer request received with email : {}", newCustomerDto.getEmail());
        log.debug("New Customer request received : {}", newCustomerDto);
        try {
            this.customerService.getCustomerByEmail(newCustomerDto.getEmail());
            this.userService.getUserByEmail(newCustomerDto.getEmail());
        }catch(ResourceNotFoundException re) {
            log.debug("No customer with this email found");
        } catch (Exception e) {
            throw new APIException("A customer with this email already exists");
        }

        CustomerDto createdCustomer = this.customerService.createCustomer(newCustomerDto);
        log.debug("New Customer created : {}", createdCustomer.getEmail());
        String jwt = this.jwtService.generateToken(this.userService.getUserByEmail(newCustomerDto.getEmail()));
        Link selfLink = linkTo(methodOn(CustomerController.class).getCustomer(createdCustomer.getCustomerId())).withSelfRel();
        Link customersLink = linkTo(methodOn(CustomerController.class).getAllCustomers()).withRel(CUSTOMERS.getValue());
        return ResponseEntity
                .created(URI.create(selfLink.getHref()))
                .header("Authorization", "Bearer " + jwt)
                .body(EntityModel.of(createdCustomer, selfLink, customersLink));
    }

    /**
     * Updates an existing customer with the given customer information.
     *
     * @param customerDto the customer information to update
     * @param customerId the ID of the customer to update
     * @return the updated customer information
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<EntityModel<CustomerDto>> updateCustomer(
            @Valid @RequestBody CustomerDto customerDto,
            @PathVariable Long customerId
    ) {
        CustomerDto updatedCustomer = this.customerService.updateCustomer(customerDto, customerId);
        Link selfLink = linkTo(CustomerController.class).slash(updatedCustomer.getCustomerId()).withSelfRel();
        return ResponseEntity.ok(EntityModel.of(updatedCustomer, selfLink));
    }

    /**
     * Deletes a customer based on the given customer ID.
     *
     * @param customerId the ID of the customer to delete
     * @return an API response indicating whether the customer was successfully deleted or not
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse> deleteCustomer(
            @PathVariable Long customerId
    ) {
        try {
            this.customerService.deleteCustomer(customerId);
            return ResponseEntity.ok(new ApiResponse(
                    "Customer deleted successfully",
                    true
            ));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns a specific Customer based on its ID.
     *
     * @param customerId the ID of the Customer to retrieve
     * @return the requested Customer, or a 404 Not Found error if the Customer does not exist
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<EntityModel<CustomerDto>> getCustomer(
            @PathVariable Long customerId
    ) {
        CustomerDto customer = this.customerService.getCustomerById(customerId);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        } else {
            Link selfLink = linkTo(CustomerController.class).slash(customer.getCustomerId()).withSelfRel();
            Link allCategoriesLink = linkTo(CustomerController.class).slash("all").withRel(CUSTOMERS.getValue());
            return ResponseEntity.ok(EntityModel.of(customer, selfLink, allCategoriesLink));
        }
    }

    /**
     * Returns a collection of all Customers.
     *
     * @return a collection of all Customers
     */
    @GetMapping
    public ResponseEntity<CollectionModel<CustomerDto>> getAllCustomers(){
        List<CustomerDto> allCategories = this.customerService.getAllCustomers();
        return new ResponseEntity<>(
                CollectionModel.of(
                        allCategories,
                        linkTo(methodOn(CustomerController.class).getAllCustomers()).withSelfRel()
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/page")
    public ResponseEntity<Page<CustomerDto>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "customerId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ){
        Page<CustomerDto> allCustomersByPage = this.customerService.getAllCustomersByPage(page, size, sortBy, sortOrder);
        return new ResponseEntity<>(allCustomersByPage,HttpStatus.OK);
    }
  
    /**
     * Returns the total number of Customers.
     *
     * @return the total number of Customers
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCustomersCount() {
        return ResponseEntity.ok(this.customerService.getTotalCustomersCount());
    }

}