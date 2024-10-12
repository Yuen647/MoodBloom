package sheerio.moodbloom.moodbloom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sheerio.moodbloom.moodbloom.dao.Interface.UserRepository;
import sheerio.moodbloom.moodbloom.dao.model.User;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 注册用户
    public User registerUser(String username, String password, String email) {
        // 检查是否用户名或邮箱已经存在
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 创建新用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setCreatedAt(LocalDateTime.now());

        // 保存新用户到数据库，并返回保存后的用户对象
        return userRepository.save(user);
    }

    // 登录用户
    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        // 校验用户名和密码
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user;
    }
    // 根据ID查找用户
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);  // 使用 JpaRepository 提供的 findById 方法
    }
}
