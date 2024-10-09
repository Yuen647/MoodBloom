package sheerio.moodbloom.moodbloom.service;

import sheerio.moodbloom.moodbloom.dao.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sheerio.moodbloom.moodbloom.dao.Interface.LocationRepository;

import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    // 保存位置信息的方法
    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    // 获取最近保存的位置信息
    public Optional<Location> getLatestLocation() {
        return locationRepository.findTopByOrderByIdDesc();
    }

    // 获取指定用户的最近位置信息
    public Optional<Location> getLatestLocationByUserId(Integer userId) {
        return locationRepository.findTopByUserIdOrderByIdDesc(userId);
    }
}
