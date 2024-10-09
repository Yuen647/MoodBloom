package sheerio.moodbloom.moodbloom.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sheerio.moodbloom.moodbloom.dao.model.Location;
import sheerio.moodbloom.moodbloom.dao.model.WeatherInfo;
import sheerio.moodbloom.moodbloom.dao.Interface.LocationRepository ;
import sheerio.moodbloom.moodbloom.service.LocationService;
import sheerio.moodbloom.moodbloom.service.WeatherService;
import sheerio.moodbloom.moodbloom.dao.Interface.WeatherInfoRepository;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private WeatherInfoRepository weatherInfoRepository;

    @Autowired
    private LocationService locationService;

    // 获取指定 Location 的天气信息
    @GetMapping("/fetch/{locationId}")
    public ResponseEntity<?> fetchWeatherForLocation(@PathVariable Integer locationId) {
        // 查找 Location 实体
        Optional<Location> locationOpt = locationRepository.findById(locationId);

        if (locationOpt.isPresent()) {
            Location location = locationOpt.get();

            try {
                // 调用 WeatherService 获取天气信息并保存到数据库
                WeatherInfo weatherInfo = weatherService.fetchWeatherInfo(location);
                return ResponseEntity.ok(weatherInfo);  // 返回天气信息
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Error fetching weather: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(404).body("Location not found with id: " + locationId);
        }
    }

    // 获取当前用户最新位置的天气信息
    @GetMapping("/current")
    public ResponseEntity<?> fetchWeatherForCurrentUser(@RequestParam("userId") Integer userId) {
        // 获取当前用户的最新位置信息
        Optional<Location> locationOpt = locationService.getLatestLocationByUserId(userId);

        if (locationOpt.isPresent()) {
            Location location = locationOpt.get();

            try {
                // 调用 WeatherService 获取天气信息
                WeatherInfo weatherInfo = weatherService.fetchWeatherInfo(location);
                return ResponseEntity.ok(weatherInfo);  // 返回天气信息
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Error fetching weather: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(404).body("No location found for user ID: " + userId);
        }
    }

    // 返回所有的天气信息
    @GetMapping("/all")
    public ResponseEntity<?> getAllWeatherInfo() {
        return ResponseEntity.ok(weatherInfoRepository.findAll());
    }
}

