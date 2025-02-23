package com.example.userapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.example.userapi.entity.User;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.cache.annotation.CacheEvict;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${external.api.url}")
    private String externalApiUrl;

    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;


    @Override
    @Transactional
    @CacheEvict(value = {"users", "userById", "userByEmail"}, allEntries = true)
    @Retryable(value = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000)) // Retries 3 times with 2s delay
    public void loadUsersFromExternalApi() {
        log.info("Starting to fetch users from external API: {}", externalApiUrl);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(externalApiUrl, Map.class);
            List<Map<String, Object>> usersList = (List<Map<String, Object>>) response.getBody().get("users");

            if (usersList == null || usersList.isEmpty()) {
                log.warn("No users found in API response");
                throw new RuntimeException("No users found in API response");
            }

            List<User> users = usersList.stream()
                    .map(this::mapToUser)
                    .collect(Collectors.toList());

            userRepository.saveAll(users);

            log.info("Successfully loaded {} users into the database.", users.size());
        } catch (Exception e) {
            log.error("Failed to load users from external API", e);
            throw new RuntimeException("Failed to load users", e);
        }
    }

    private User mapToUser(Map<String, Object> userMap) {
        User user = new User();
        user.setFirstName((String) userMap.get("firstName"));
        user.setLastName((String) userMap.get("lastName"));
        user.setEmail((String) userMap.get("email"));
        user.setSsn((String) userMap.get("ssn"));
        user.setAge((Integer) userMap.get("age"));
        user.setRole((String) userMap.get("role"));
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#text") // Cache search results
    public List<User> searchUsers(String text) {
        log.info("Searching users with text: {}", text);

        SearchSession searchSession = Search.session(entityManager);
        SearchResult<User> result = searchSession.search(User.class)
                .where(f -> f.bool()
                        .should(f.wildcard().fields("firstName", "lastName").matching("*" + text + "*"))
                        .should(f.match().field("email").matching(text))
                        .should(f.match().field("ssn").matching(text))
                )
                .fetch(20);

        log.info("Found {} users for search text: {}", result.hits().size(), text);
        return result.hits();
    }

    @Override
    public User findUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        return userRepository.findById(id).orElseThrow(() -> {
            log.warn("User not found with ID: {}", id);
            return new RuntimeException("User not found");
        });
    }

    @Override
    @Cacheable(value = "userByEmail", key = "#email")
    public User findUserByEmail(String email) {
        log.info("Fetching user with Email: {}", email);
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("User not found with Email: {}", email);
            return new RuntimeException("User not found");
        });
    }
}
