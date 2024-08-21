package com.vedasole.ekartecommercebackend.utility;

import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.CustomerDto;
import com.vedasole.ekartecommercebackend.service.serviceInterface.CustomerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final CustomerService customerService;
    private final String adminPassword;

    public ApplicationInitializer(
            CustomerService customerService,
            @Value("${admin.password:Admin@123}") String adminPassword
    ) {
        this.customerService = customerService;
        this.adminPassword = adminPassword;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        insertAdminUser();
    }

    private void insertAdminUser() {
        String adminEmail = "admin@ekart.com";
        try {
            customerService.getCustomerByEmail(adminEmail);
        } catch (ResourceNotFoundException e) {
            CustomerDto adminUser = CustomerDto.builder()
                    .customerId(1)
                    .email(adminEmail)
                    .firstName("Admin")
                    .lastName("User")
                    .phoneNumber("1234567890")
                    .password(adminPassword)
                    .role(AppConstant.Role.ADMIN)
                    .build();
            customerService.createCustomer(adminUser);
        }
    }
}