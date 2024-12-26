package com.vedasole.ekartecommercebackend.service.service_impl;

import com.vedasole.ekartecommercebackend.entity.Customer;
import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.exception.APIException;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.CustomerDto;
import com.vedasole.ekartecommercebackend.payload.NewCustomerDto;
import com.vedasole.ekartecommercebackend.repository.CustomerRepo;
import com.vedasole.ekartecommercebackend.repository.ShoppingCartRepo;
import com.vedasole.ekartecommercebackend.repository.UserRepo;
import com.vedasole.ekartecommercebackend.service.service_interface.CustomerService;
import com.vedasole.ekartecommercebackend.utility.AppConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.CUSTOMER;
import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.USER;

/**
 * This class provides the implementation for the Customer Service.
 * It uses the CustomerRepository and UserRepository to perform operations on the database.
 */
@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepo customerRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ShoppingCartRepo shoppingCartRepo;
    private final EmailServiceImpl emailService;

    /**
     * Creates a new customer and saves it to the database.
     *
     * @param newCustomerDto the new customer data to be saved
     * @return the created customer with its ID and other details
     * @throws APIException if an error occurs while saving the customer
     */
    @Override
    @CacheEvict(value = "allCustomers", allEntries = true)
    @Caching(evict = {
            @CacheEvict(value = "allCustomers", allEntries = true),
            @CacheEvict(value = "allCustomersPage", allEntries = true)
    })
    public CustomerDto createCustomer(@Valid NewCustomerDto newCustomerDto) {
        // Create a new user with the role of USER
        User user = new User(
                newCustomerDto.getEmail(),
                passwordEncoder.encode(newCustomerDto.getPassword()),
                newCustomerDto.getRole() != null ? newCustomerDto.getRole() : AppConstant.Role.USER
        );
        // Save the user to the database
        User newUser;
        try {
            newUser = this.userRepo.save(user);
            log.debug("Saved new user with email: {}", newUser.getEmail());
        } catch (Exception e) {
            throw new APIException("Failed to save user");
        }

        // Create a new customer and set the user to the newly created user
        Customer customer = newCustomerDtoToCustomer(newCustomerDto);
        customer.setUser(newUser);

        // Save the customer to the database
        Customer newCustomer;
        try {
            newCustomer = this.customerRepo.save(customer);
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setCustomer(newCustomer);
            shoppingCart = shoppingCartRepo.save(shoppingCart);
            newCustomer.setShoppingCart(shoppingCart);
            newCustomer = this.customerRepo.save(newCustomer);
            log.debug("Customer saved with id : {}", newCustomer.getEmail());
        } catch (Exception e) {
            log.error("Failed to save customer: {}", newCustomerDto.getEmail());
            throw new APIException("Failed to save customer");
        }

        // Send welcome email
        try{
            sendWelcomeEmail(newUser.getEmail(), newCustomer.getFirstName());
            log.debug("Welcome email send to the user with email: {}", newUser.getEmail());
        } catch (MessagingException e) {
            log.error("Error sending welcome email to new customer with email: {}", newUser.getEmail(), e);
        }

        log.info("New user created with email: {}", newCustomer.getEmail());
        return customerToDto(newCustomer);
    }

    /**
     * Updates an existing customer.
     *
     * @param customerDto the customer data to be updated
     * @param customerId the ID of the customer to be updated
     * @return the updated customer with its ID and other details
     * @throws APIException if an error occurs while updating the customer
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "customers", key = "#customerId"),
            @CacheEvict(value = "allCustomers", allEntries = true),
            @CacheEvict(value = "allCustomersPage", allEntries = true)
    })
    public CustomerDto updateCustomer(CustomerDto customerDto, Long customerId) {

        Customer customer = dtoToCustomer(customerDto);

        Customer customerFromDB = this.customerRepo.findById(customerId).
                orElseThrow(() -> new ResourceNotFoundException(
                        CUSTOMER.getValue(), "id", customerId));

        Long userId = customerFromDB.getUser().getUserId();

        User userForCustomerInDB = this.userRepo.findById(userId).
                orElseThrow(() -> new ResourceNotFoundException(
                        USER.getValue(), "id", userId));

        userForCustomerInDB.setEmail(customerDto.getEmail());
        Optional.ofNullable(customerDto.getRole())
                .ifPresent(userForCustomerInDB::setRole);

        User updatedUser = this.userRepo.save(userForCustomerInDB);

        customerFromDB.setUser(updatedUser);
        customerFromDB.setEmail(customerDto.getEmail());
        customerFromDB.setFirstName(customer.getFirstName());
        customerFromDB.setLastName(customer.getLastName());
        customerFromDB.setPhoneNumber(customer.getPhoneNumber());

        Customer updatedCustomer = this.customerRepo.save(customerFromDB);

        return customerToDto(updatedCustomer);
    }

    /**
     * Deletes a customer based on its ID.
     *
     * @param customerId the ID of the customer to be deleted
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "customers", key = "#customerId"),
            @CacheEvict(value = "allCustomers", allEntries = true),
            @CacheEvict(value = "allCustomersPage", allEntries = true)
    })
    public void deleteCustomer(Long customerId) {
        this.customerRepo.deleteById(customerId);
    }

    /**
     * Returns a list of all customers.
     *
     * @return a list of all customers
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allCustomers", sync = true)
    public List<CustomerDto> getAllCustomers() {
        return this.customerRepo.findAll().stream()
                .map(this::customerToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "allCustomersPage",
            key = "#page + '-' + #size + '-' + #sortBy + '-' + #sortOrder",
            sync = true
    )
    public Page<CustomerDto> getAllCustomersByPage(int page, int size, String sortBy, String sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        return this.customerRepo.findAll(pageRequest)
                .map(this::customerToDto);
    }

    /**
     * Returns a customer based on its ID.
     *
     * @param customerId the ID of the customer to be retrieved
     * @return the customer with the specified ID
     * @throws ResourceNotFoundException if the customer is not found
     */
    @Override
    @Cacheable(value = "customers", key = "#customerId")
    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long customerId) {
        return this.customerRepo.findById(customerId)
                .map(this::customerToDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CUSTOMER.getValue(), "id", customerId)
                );
    }

    /**
     * Returns a customer based on the email.
     *
     * @param email the email id of the customer to be retrieved
     * @return the customer with the specified email
     * @throws ResourceNotFoundException if the customer is not found
     */
    @Override
    @Transactional(readOnly = true)
    public CustomerDto getCustomerByEmail(String email) {
        return this.customerRepo.findByEmail(email)
                .map(this::customerToDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CUSTOMER.getValue(), "email", email)
                );
    }


    /**
     * Returns the user associated with a customer.
     *
     * @param customerId the ID of the customer
     * @return the user associated with the customer
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserForCustomer(Long customerId) {
        return this.customerRepo.findById(customerId)
                .map(Customer::getUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CUSTOMER.getValue(), "id", customerId)
                );
    }

    /**
     * Returns the total number of customers.
     *
     * @return the total number of customers
     */
    @Override
    @Transactional(readOnly = true)
    public Long getTotalCustomersCount() {
        return this.customerRepo.count();
    }

    /**
     * Sends a welcome email to a new customer.
     *
     * @param email the email address of the recipient
     * @param firstName the first name of the recipient to personalize the email
     * @throws MessagingException if an error occurs while sending the email
     */
    private void sendWelcomeEmail(String email, String firstName) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", firstName);
        emailService.sendMimeMessage(email,"Welcome to Ekart Shopping!", context, "welcome");
    }

    /**
     * Converts a CustomerDto object to a Customer entity.
     *
     * @param customerDto the CustomerDto object containing customer data to be converted
     * @return a Customer entity mapped from the provided CustomerDto
     */
    @Override
    public Customer convertToCustomer(CustomerDto customerDto) {
        return dtoToCustomer(customerDto);
    }

    /**
     * Converts a Customer entity to a CustomerDto object.
     *
     * @param customer the Customer entity containing customer data to be converted
     * @return a CustomerDto object mapped from the provided Customer entity
     */
    @Override
    public CustomerDto convertToCustomerDto(Customer customer) {
        return customerToDto(customer);
    }


    /**
     * Maps a CustomerDto to a Customer.
     *
     * @param customerDto the CustomerDto to be mapped
     * @return the mapped Customer
     */
    private Customer dtoToCustomer(CustomerDto customerDto){
        User user = new User(customerDto.getEmail(), customerDto.getPassword(), customerDto.getRole());
        Customer customer = this.modelMapper.map(customerDto, Customer.class);
        customer.setUser(user);
        return customer;
    }

    /**
     * Maps a NewCustomerDto to a Customer.
     *
     * @param newCustomerDto the NewCustomerDto to be mapped
     * @return the mapped Customer
     */
    private Customer newCustomerDtoToCustomer(NewCustomerDto newCustomerDto){
        User user = new User(newCustomerDto.getEmail(), passwordEncoder.encode(newCustomerDto.getPassword()), newCustomerDto.getRole());
        Customer customer = this.modelMapper.map(newCustomerDto, Customer.class);
        customer.setUser(user);
        return customer;
    }

    /**
     * Maps a Customer to a CustomerDto.
     *
     * @param customer the Customer to be mapped
     * @return the mapped CustomerDto
     */
    private CustomerDto customerToDto(Customer customer)
    {
        CustomerDto customerDto = this.modelMapper.map(customer, CustomerDto.class);
        customerDto.setEmail(customer.getUser().getEmail());
        customerDto.setRole(customer.getUser().getRole());
        return customerDto;
    }

}