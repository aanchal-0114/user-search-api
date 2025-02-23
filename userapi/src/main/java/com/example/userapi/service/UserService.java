package com.example.userapi.service;
import org.springframework.cache.annotation.Cacheable;
import com.example.userapi.entity.User;
import java.util.List;

public interface UserService {
    void loadUsersFromExternalApi();
    List<User> searchUsers(String text);
    User findUserById(Long id);
    User findUserByEmail(String email);
}
