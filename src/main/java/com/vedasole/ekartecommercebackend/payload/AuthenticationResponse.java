package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.utility.AppConstant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
@AllArgsConstructor
@Data
public class AuthenticationResponse {

    private String token;
    private String firstName;
    private String lastName;
    private long customerId;
    private String email;
    private Role role;
    private long cartId;

}
