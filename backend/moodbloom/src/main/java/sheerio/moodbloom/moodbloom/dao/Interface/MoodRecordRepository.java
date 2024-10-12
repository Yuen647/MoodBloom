package sheerio.moodbloom.moodbloom.dao.Interface;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import sheerio.moodbloom.moodbloom.dao.model.MoodRecord;
import sheerio.moodbloom.moodbloom.dao.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MoodRecordRepository extends JpaRepository<MoodRecord, Integer> {
    List<MoodRecord> findByUserId(Integer userId);
    List<MoodRecord> findByUserIdAndRecordTimeBetween(Integer userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    MoodRecord findByIdAndUserId(Integer id, Integer userId);
}