package com.example.userapi.service.impl;

import com.example.userapi.entity.User;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.service.UserService;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.scope.SearchScope;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityManager;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private String externalApiUrl = "https://dummyjson.com/users";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setSsn("123-45-6789");
        user.setAge(30);
        user.setRole("admin");
        ReflectionTestUtils.setField(userService, "externalApiUrl", "https://dummyjson.com/users");
    }

    @Test
    void loadUsersFromExternalApi_Success() {
        // Mock API response
        Map<String, Object> apiResponse = new HashMap<>();
        List<Map<String, Object>> usersList = List.of(
                Map.of("id", 1, "firstName", "John", "lastName", "Doe", "email", "john@example.com")
        );
        apiResponse.put("users", usersList);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(apiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);
        // Mock user repository save
        when(userRepository.saveAll(anyList())).thenReturn(List.of(user));

        // Call method
        userService.loadUsersFromExternalApi();

        // Verify interactions
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Map.class));
        verify(userRepository, times(1)).saveAll(anyList());
    }
    @Test
    void loadUsersFromExternalApi_EmptyResponse_ThrowsException() {
        // Mock API response with empty user list
        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("users", Collections.emptyList());

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(apiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        // Expect RuntimeException
        assertThrows(RuntimeException.class, () -> userService.loadUsersFromExternalApi());

        // Verify interactions
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Map.class));
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    void testFindUserByEmaill_Success() {
        // ✅ Correctly mock repository behavior
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        // Act
        User result = userService.findUserByEmail("john.doe@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void testFindUserById() {
        // Mock repository
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // Call the method
        User result = userService.findUserById(1L);
        // Verify interactions
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void testFindUserById_NotFound() {
        // Mock repository
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the method and expect exception
        assertThrows(RuntimeException.class, () -> userService.findUserById(1L));
    }

    @Test
    void testFindUserByEmail() {
        // Mock repository
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        // Call the method
        User result = userService.findUserByEmail("john.doe@example.com");

        // Verify interactions
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void testFindUserByEmail_NotFound() {
        // Mock repository
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        // Call the method and expect exception
        assertThrows(RuntimeException.class, () -> userService.findUserByEmail("john.doe@example.com"));
    }
}