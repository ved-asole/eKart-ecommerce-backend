package com.vedasole.ekartecommercebackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EkartEcommerceBackendApplicationTests {

    @Autowired
    EkartEcommerceBackendApplication application = new EkartEcommerceBackendApplication();

    @Test
    void contextLoads() {

        assertNotNull(application);

    }

}
