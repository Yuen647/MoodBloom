package sheerio.moodbloom.moodbloom.dao.Interface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sheerio.moodbloom.moodbloom.dao.model.WeatherInfo;

import java.util.List;

@Repository
public interface WeatherInfoRepository extends JpaRepository<WeatherInfo, Integer> {

}

