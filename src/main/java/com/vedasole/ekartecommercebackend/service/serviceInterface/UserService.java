package com.vedasole.ekartecommercebackend.service.serviceInterface;

import com.vedasole.ekartecommercebackend.entity.User;

import java.util.List;

public interface UserService {

    public User createUser(User user);

    public User updateUser(User user, Long userId);

    boolean deleteUser(Long userId);

    public User getUserByEmail(String email);

    public User getUserById(Long userId);

    public List<User> getAllUsers();

}
