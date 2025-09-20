# Jakarta EE Migration Guide for Spring Boot 3.5.3

This guide provides instructions for migrating from Java EE (javax.*) to Jakarta EE (jakarta.*) for Spring Boot 3.5.3
compatibility.

## Background

Spring Boot 3.x is based on Jakarta EE 9+, which renamed all packages from `javax.*` to `jakarta.*`. This migration
guide will help you update your codebase to use the new Jakarta EE packages.

## Migration Steps

### 1. Update Maven Dependencies

The pom.xml file has been updated to use Spring Boot 3.5.3 and Java 21:

```xml

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.3</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
<properties>
<java.version>21</java.version>
<!-- other properties -->
</properties>
```

### 2. Update Java EE Imports to Jakarta EE

The following package mappings need to be updated:

| Java EE (Old)       | Jakarta EE (New)      |
|---------------------|-----------------------|
| javax.validation.*  | jakarta.validation.*  |
| javax.persistence.* | jakarta.persistence.* |
| javax.servlet.*     | jakarta.servlet.*     |
| javax.mail.*        | jakarta.mail.*        |
| javax.transaction.* | jakarta.transaction.* |

### 3. Update Swagger Configuration

Springfox is not compatible with Spring Boot 3.x. It has been replaced with SpringDoc OpenAPI:

```xml

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.4.0</version>
</dependency>
```

The SwaggerConfig.java file has been updated to use SpringDoc OpenAPI instead of Springfox.

### 4. Update Security Configuration

Spring Security 6.x (which comes with Spring Boot 3.x) has significant changes in its API. The WebSecurityConfig.java
file has been updated to use the new approach:

- Replaced `@EnableGlobalMethodSecurity` with `@EnableMethodSecurity`
- Replaced `antMatchers()` with `requestMatchers()`
- Updated the lambda-based configuration style
- Updated the Swagger URLs for SpringDoc OpenAPI

### 5. Update Database Configuration

The Hibernate dialect has been updated in the application properties files:

- Replaced `org.hibernate.dialect.PostgreSQL10Dialect` with `org.hibernate.dialect.PostgreSQLDialect`

### 6. Update Redis Configuration

The Redis configuration properties have been updated:

- Replaced `spring.redis.host` with `spring.data.redis.host`
- Replaced `spring.redis.port` with `spring.data.redis.port`

## Files to Update

The following files need to be updated with Jakarta EE imports:

### javax.validation.* -> jakarta.validation.*

```
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/payload/AddressDto.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/payload/ProductDto.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/test/java/com/vedasole/ekartecommercebackend/payload/CustomerDtoTest.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/controller/ProductController.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/Customer.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/Product.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/User.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/payload/CategoryDto.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/payload/OrderDto.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/payload/ShoppingCartDto.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/Order.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/payload/OrderItemDto.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/payload/ShoppingCartItemDto.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/controller/CategoryController.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/controller/CustomerController.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/controller/OrderController.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/controller/OrderItemController.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/controller/PaymentController.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/controller/ShoppingCartController.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/controller/ShoppingCartItemController.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/OrderItem.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/ShoppingCart.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/ShoppingCartItem.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/payload/CustomerDto.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/payload/NewCustomerDto.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/payload/PasswordResetRequestDto.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/service/service_impl/CustomerServiceImpl.java
```

### javax.persistence.* -> jakarta.persistence.*

```
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/Customer.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/Product.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/User.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/payload/CustomerDto.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/payload/NewCustomerDto.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/Order.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/OrderItem.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/ShoppingCart.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/ShoppingCartItem.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/Address.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/Category.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/entity/PasswordResetToken.java
```

### javax.servlet.* -> jakarta.servlet.*

```
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/security/JWTAuthenticationFilter.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/security/JWTAuthenticationEntryPoint.java
```

### javax.mail.* -> jakarta.mail.*

```
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/service/service_impl/CustomerServiceImpl.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/service/service_impl/PaymentServiceImpl.java
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/test/java/com/vedasole/ekartecommercebackend/config/TestMailConfig.java
```

### javax.transaction.* -> jakarta.transaction.*

```
/Users/hendisantika/IdeaProjects/eKart-ecommerce-backend/src/main/java/com/vedasole/ekartecommercebackend/service/service_impl/OrderItemServiceImpl.java
```

## Automated Migration

You can use the following command to automatically update all javax.* imports to jakarta.* in your project:

```bash
find . -type f -name "*.java" -exec sed -i '' 's/javax\.validation/jakarta.validation/g' {} \;
find . -type f -name "*.java" -exec sed -i '' 's/javax\.persistence/jakarta.persistence/g' {} \;
find . -type f -name "*.java" -exec sed -i '' 's/javax\.servlet/jakarta.servlet/g' {} \;
find . -type f -name "*.java" -exec sed -i '' 's/javax\.mail/jakarta.mail/g' {} \;
find . -type f -name "*.java" -exec sed -i '' 's/javax\.transaction/jakarta.transaction/g' {} \;
```

## Verification

After updating all the files, build the project to verify that the changes work correctly:

```bash
mvn clean install
```

If there are any build errors, address them one by one until the build succeeds.

## Testing

Run the tests to ensure that the functionality is maintained:

```bash
mvn test
```

If there are any test failures, address them one by one until all tests pass.