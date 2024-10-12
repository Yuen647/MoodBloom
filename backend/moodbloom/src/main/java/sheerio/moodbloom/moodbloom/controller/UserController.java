package sheerio.moodbloom.moodbloom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sheerio.moodbloom.moodbloom.dao.model.User;
import sheerio.moodbloom.moodbloom.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> userDTO) {
        String username = userDTO.get("username");
        String password = userDTO.get("password");
        String email = userDTO.get("email");

        System.out.println("Attempting to register user with username: " + username + " and email: " + email);

        try {
            User user = userService.registerUser(username, password, email);
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("createdAt", user.getCreatedAt());
            System.out.println("User registered successfully: " + user.getUsername());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.out.println("Registration error: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // 用户登录接口
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginDTO) {
        String username = loginDTO.get("username");
        String password = loginDTO.get("password");

        try {
            User user = userService.loginUser(username, password);
            // 构建单独的响应结构，直接返回 userId 等字段
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
