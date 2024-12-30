package com.vedasole.ekartecommercebackend.utility;

import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.NewCustomerDto;
import com.vedasole.ekartecommercebackend.security.JwtService;
import com.vedasole.ekartecommercebackend.service.service_interface.CustomerService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.event.ApplicationEventsTestExecutionListener;

@Component
@Getter
@Slf4j
public class TestApplicationInitializer extends ApplicationEventsTestExecutionListener {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomerService customerService;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${normalUser.email}")
    private String normalUserEmail;

    @Value("${normalUser.password}")
    private String normalUserPassword;

    private String adminToken=null;
    private String userToken=null;

    @EventListener(ApplicationReadyEvent.class)
    public void generateTokens() {
        log.info("ApplicationReadyEvent triggered and generating tokens");
        insertAdminUser();
        generateAdminToken();
        insertNormalUser();
        generateUserToken();
    }

    private void insertAdminUser() {
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

    private void generateAdminToken() {
        User user = new User();
        user.setEmail(adminEmail);
        user.setPassword(adminPassword);
        this.adminToken = jwtService.generateToken(user);
        log.info("admin token generated");
    }

    private void generateUserToken() {
        User user = new User();
        user.setEmail(normalUserEmail);
        user.setPassword(normalUserPassword);
        this.userToken = jwtService.generateToken(user);
        log.info("user token generated");
    }

    private void insertNormalUser() {
        NewCustomerDto normalUser = NewCustomerDto.builder()
                    .customerId(2)
                    .email(normalUserEmail)
                    .firstName("Normal")
                    .lastName("User")
                    .phoneNumber("1234567890")
                    .password(normalUserPassword)
                    .role(AppConstant.Role.USER)
                    .build();
        customerService.createCustomer(normalUser);
    }

}