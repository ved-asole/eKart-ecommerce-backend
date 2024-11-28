package com.vedasole.ekartecommercebackend.service.service_interface;

import com.vedasole.ekartecommercebackend.entity.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User updateUser(User user, Long userId);

    boolean deleteUser(Long userId);

    User getUserByEmail(String email);

    User getUserById(Long userId);

    List<User> getAllUsers();

}