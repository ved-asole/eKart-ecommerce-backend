package com.vedasole.ekartecommercebackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vedasole.ekartecommercebackend.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@AllArgsConstructor
public class ApplicationConfig
{
    private final UserRepo userRepo;


    /**
     * Creates a new instance of the MapperConfig class.
     *
     * @return a new MapperConfig instance
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper;
    }

    /**
     * Creates a new instance of the ObjectMapper class.
     *
     * @return a new ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    /**
     * Creates a new instance of the UserDetailsService class.
     *
     * @return a new UserDetailsService instance
     */
    @Bean
    public UserDetailsService userDetailsService(){
      return username -> this.userRepo.findByEmailIgnoreCase(username)
           .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Creates a new instance of the AuthenticationProvider class.
     *
     * @return a new AuthenticationProvider instance
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Creates a new instance of the AuthenticationManager class.
     *
     * @return a new AuthenticationManager instance
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Creates a new instance of the PasswordEncoder class.
     *
     * @return a new BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}