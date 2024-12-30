package com.vedasole.ekartecommercebackend.utility;

import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.NewCustomerDto;
import com.vedasole.ekartecommercebackend.service.service_interface.CustomerService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"prod","uat","dev"})
public class ApplicationInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final CustomerService customerService;
    private final String adminPassword;

    public ApplicationInitializer(
            CustomerService customerService,
            @Value("${admin.password}") String adminPassword
    ) {
        this.customerService = customerService;
        this.adminPassword = adminPassword;
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        insertAdminUser();
    }

    private void insertAdminUser() {
        String adminEmail = "admin@ekart.com";
        try {
            customerService.getCustomerByEmail(adminEmail);
        } catch (ResourceNotFoundException e) {
            NewCustomerDto adminUser = NewCustomerDto.builder()
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