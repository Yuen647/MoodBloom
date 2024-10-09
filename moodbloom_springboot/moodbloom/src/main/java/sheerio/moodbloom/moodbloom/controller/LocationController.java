package sheerio.moodbloom.moodbloom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sheerio.moodbloom.moodbloom.dao.model.Location;
import sheerio.moodbloom.moodbloom.dao.model.User;
import sheerio.moodbloom.moodbloom.service.LocationService;
import sheerio.moodbloom.moodbloom.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = "http://127.0.0.1:5501")  // 允许从该前端地址访问
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    // 接收前端的POST请求，保存位置信息到数据库
    @PostMapping("/save")
    public ResponseEntity<Location> saveLocation(@RequestBody Location location, @RequestParam Integer userId) {
        // 根据 userId 获取 User 实体
        User user = userService.findById(userId);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 如果 userId 无效，返回 404
        }

        // 将 User 设置到 Location 中
        location.setUser(user);

        // 调用服务层保存位置信息
        Location savedLocation = locationService.saveLocation(location);

        // 返回保存后的位置信息
        return new ResponseEntity<>(savedLocation, HttpStatus.CREATED);
    }

    @GetMapping("/current")
    public ResponseEntity<?> getLatestLocation(@RequestParam("userId") Integer userId) {
        try {
            // 通过用户ID获取最新的位置信息
            Optional<Location> locationOpt = locationService.getLatestLocationByUserId(userId);
            Map<String, Object> result = new HashMap<>();

            if (locationOpt.isPresent()) {
                Location location = locationOpt.get();
                result.put("location", location);  // 将位置信息放入返回结果中
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(404).body("用户ID " + userId + " 没有找到位置信息");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }



}
