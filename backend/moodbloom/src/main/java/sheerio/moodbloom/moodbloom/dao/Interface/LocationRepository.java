package sheerio.moodbloom.moodbloom.dao.Interface;

import org.springframework.data.jpa.repository.JpaRepository;
import sheerio.moodbloom.moodbloom.dao.model.Location;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    // 获取最近保存的位置信息
    Optional<Location> findTopByOrderByIdDesc();

    Optional<Location> findTopByUserIdOrderByIdDesc(Integer userId);
}
