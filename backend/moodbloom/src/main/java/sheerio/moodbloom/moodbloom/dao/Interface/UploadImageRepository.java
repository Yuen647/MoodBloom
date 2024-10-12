package sheerio.moodbloom.moodbloom.dao.Interface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sheerio.moodbloom.moodbloom.dao.model.MoodRecord;
import sheerio.moodbloom.moodbloom.dao.model.UploadImage;

import java.util.List;


@Repository
public interface UploadImageRepository extends JpaRepository<UploadImage, Integer> {
    List<UploadImage> findByMoodRecord(MoodRecord moodRecord);

    // 根据心情记录ID查询图片
    List<UploadImage> findByMoodRecordId(Integer moodRecordId);
}

