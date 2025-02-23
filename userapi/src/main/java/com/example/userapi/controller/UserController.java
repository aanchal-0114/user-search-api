package com.example.userapi.controller;
import com.example.userapi.entity.User;
import com.example.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("/api/users")
@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
public class UserController {
    private final UserService userService;

    @PostMapping("/load")
    public ResponseEntity<String> loadUsers() {
        userService.loadUsersFromExternalApi();
        return ResponseEntity.ok("Users loaded successfully.");
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }
}