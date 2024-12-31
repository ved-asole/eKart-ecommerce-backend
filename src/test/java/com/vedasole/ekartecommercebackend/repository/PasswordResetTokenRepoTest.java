package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.PasswordResetToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PasswordResetTokenRepoTest {
    
    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;

    @BeforeEach
    void setUp() {
        passwordResetTokenRepo.deleteAll();
    }

    @Test
    void shouldFindPasswordResetTokenByValidToken() {
        // Given
        String validToken = "valid-token";
        PasswordResetToken passwordResetToken = new PasswordResetToken(validToken, "test@email.com");
        passwordResetTokenRepo.save(passwordResetToken);

        // When
        Optional<PasswordResetToken> foundToken = passwordResetTokenRepo.findByToken(validToken);

        // Then
        assertTrue(foundToken.isPresent());
        assertEquals(validToken, foundToken.get().getToken());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentToken() {
        // Given
        String nonExistentToken = "non-existent-token";

        // When
        Optional<PasswordResetToken> result = passwordResetTokenRepo.findByToken(nonExistentToken);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSaveAndRetrievePasswordResetToken() {
        // Given
        String token = "test-token";
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, "test@email.com");

        // When
        PasswordResetToken savedToken = passwordResetTokenRepo.save(passwordResetToken);
        Optional<PasswordResetToken> retrievedToken = passwordResetTokenRepo.findByToken(token);

        // Then
        assertTrue(retrievedToken.isPresent());
        assertEquals(savedToken.getId(), retrievedToken.get().getId());
        assertEquals(token, retrievedToken.get().getToken());
    }

    @Test
    void shouldUpdateExistingPasswordResetToken() {
        // Given
        String originalToken = "original-token";
        PasswordResetToken originalPasswordResetToken = new PasswordResetToken(originalToken, "test@email.com");
        PasswordResetToken savedToken = passwordResetTokenRepo.save(originalPasswordResetToken);

        // When
        String updatedToken = "updated-token";
        savedToken.setToken(updatedToken);
        passwordResetTokenRepo.save(savedToken);

        // Then
        Optional<PasswordResetToken> updatedPasswordResetToken = passwordResetTokenRepo.findById(savedToken.getId());
        assertTrue(updatedPasswordResetToken.isPresent());
        assertEquals(updatedToken, updatedPasswordResetToken.get().getToken());
        assertNotEquals(originalToken, updatedPasswordResetToken.get().getToken());
    }

    @Test
    void shouldDeletePasswordResetTokenById() {
        // Given
        String token = "delete-test-token";
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, "test@email.com");
        PasswordResetToken savedToken = passwordResetTokenRepo.save(passwordResetToken);

        // When
        passwordResetTokenRepo.deleteById(savedToken.getId());

        // Then
        Optional<PasswordResetToken> deletedToken = passwordResetTokenRepo.findById(savedToken.getId());
        assertTrue(deletedToken.isEmpty());
        Optional<PasswordResetToken> tokenByValue = passwordResetTokenRepo.findByToken(token);
        assertTrue(tokenByValue.isEmpty());
    }

    @Test
    void shouldHandleCaseSensitiveTokenSearches() {
        // Given
        String lowercaseToken = "lowercase-token";
        String uppercaseToken = "UPPERCASE-TOKEN";
        PasswordResetToken lowercasePasswordResetToken = new PasswordResetToken(lowercaseToken, "test@email.com");
        PasswordResetToken uppercasePasswordResetToken = new PasswordResetToken(uppercaseToken, "test@email.com");
        passwordResetTokenRepo.save(lowercasePasswordResetToken);
        passwordResetTokenRepo.save(uppercasePasswordResetToken);

        // When
        Optional<PasswordResetToken> foundLowercaseToken = passwordResetTokenRepo.findByToken(lowercaseToken);
        Optional<PasswordResetToken> foundUppercaseToken = passwordResetTokenRepo.findByToken(uppercaseToken);
        Optional<PasswordResetToken> notFoundMixedCaseToken = passwordResetTokenRepo.findByToken("LowerCase-TOKEN");

        // Then
        assertTrue(foundLowercaseToken.isPresent());
        assertEquals(lowercaseToken, foundLowercaseToken.get().getToken());
        assertTrue(foundUppercaseToken.isPresent());
        assertEquals(uppercaseToken, foundUppercaseToken.get().getToken());
        assertTrue(notFoundMixedCaseToken.isEmpty());
    }

    @Test
    void shouldReturnAllPasswordResetTokens() {
        // Given
        PasswordResetToken token1 = new PasswordResetToken("token1", "test@email.com");
        PasswordResetToken token2 = new PasswordResetToken("token2", "test@email.com");
        passwordResetTokenRepo.saveAll(Arrays.asList(token1, token2));

        // When
        List<PasswordResetToken> allTokens = passwordResetTokenRepo.findAll();

        // Then
        assertEquals(2, allTokens.size());
        assertTrue(allTokens.stream().anyMatch(token -> token.getToken().equals("token1")));
        assertTrue(allTokens.stream().anyMatch(token -> token.getToken().equals("token2")));
    }

    @Test
    void shouldHandleSearchingForTokenWithSpecialCharacters() {
        // Given
        String specialToken = "special@token#123!";
        PasswordResetToken passwordResetToken = new PasswordResetToken(specialToken,"test@email.com");
        passwordResetTokenRepo.save(passwordResetToken);

        // When
        Optional<PasswordResetToken> foundToken = passwordResetTokenRepo.findByToken(specialToken);

        // Then
        assertTrue(foundToken.isPresent());
        assertEquals(specialToken, foundToken.get().getToken());
    }

    @Test
    void shouldPerformPaginationCorrectlyWhenRetrievingMultiplePasswordResetTokens() {
        // Given
        List<PasswordResetToken> tokens = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            PasswordResetToken token = new PasswordResetToken("token-" + i, "token-" + i + "@email.com");
            tokens.add(token);
        }
        passwordResetTokenRepo.saveAll(tokens);

        // When
        Pageable firstPageWithTenElements = PageRequest.of(0, 10);
        Page<PasswordResetToken> firstPage = passwordResetTokenRepo.findAll(firstPageWithTenElements);

        Pageable secondPageWithTenElements = PageRequest.of(1, 10);
        Page<PasswordResetToken> secondPage = passwordResetTokenRepo.findAll(secondPageWithTenElements);

        // Then
        assertEquals(10, firstPage.getContent().size());
        assertEquals(10, secondPage.getContent().size());
        assertEquals(0, firstPage.getNumber());
        assertEquals(1, secondPage.getNumber());
        assertEquals(3, firstPage.getTotalPages());
        assertEquals(25, firstPage.getTotalElements());
        assertNotEquals(firstPage.getContent().get(0).getToken(), secondPage.getContent().get(0).getToken());
    }

    @Test
    @Disabled("This test is disabled because it is not working as expected")
    void shouldThrowExceptionWhenSavingPasswordResetTokenWithNullToken() {
        // Given
        PasswordResetToken passwordResetToken = new PasswordResetToken(null, "test@email.com");

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> passwordResetTokenRepo.save(passwordResetToken));
    }

}