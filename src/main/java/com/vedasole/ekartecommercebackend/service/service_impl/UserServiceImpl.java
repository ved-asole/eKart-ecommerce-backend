package com.vedasole.ekartecommercebackend.service.service_impl;

import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.exception.APIException;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.repository.UserRepo;
import com.vedasole.ekartecommercebackend.service.service_interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(this.userRepo.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new APIException("User with email " + user.getEmail() + " already exists", HttpStatus.BAD_REQUEST);
        }
        return this.userRepo.save(user);
    }

    @Override
    @CacheEvict(value = "users")
    public User updateUser(User user, Long userId) {

        User savedUser = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User", "userId", userId
                ));

        savedUser.setEmail(user.getEmail());
        savedUser.setRole(user.getRole());
        if (!savedUser.getPassword().equals(user.getPassword())) {
            savedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return this.userRepo.save(savedUser);
    }

    @Override
    @CacheEvict(value = "users")
    public boolean deleteUser(Long userId) {
        if (userId == null) {
            log.error("Invalid userId received to delete user : null");
            throw new IllegalArgumentException("Invalid userId received to delete user : null");
        }
        try{
            this.userRepo.deleteById(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Cacheable(value = "users")
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "userId", userId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return this.userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return this.userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "email", email
                ));
    }
}