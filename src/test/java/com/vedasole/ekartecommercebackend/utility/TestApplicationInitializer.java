package com.vedasole.ekartecommercebackend.utility;

import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.payload.CustomerDto;
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
        generateAdminToken();
        insertNormalUser();
        generateUserToken();
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
        String userEmail = "normal-user@ekart.com";
        CustomerDto adminUser = CustomerDto.builder()
                    .customerId(2)
                    .email(userEmail)
                    .firstName("Normal")
                    .lastName("User")
                    .phoneNumber("1234567890")
                    .password("normal-user-test-adminPassword")
                    .role(AppConstant.Role.USER)
                    .build();
        customerService.createCustomer(adminUser);
    }

}