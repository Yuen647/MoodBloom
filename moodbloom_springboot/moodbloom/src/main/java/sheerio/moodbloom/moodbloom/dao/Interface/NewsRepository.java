package sheerio.moodbloom.moodbloom.dao.Interface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sheerio.moodbloom.moodbloom.dao.model.News;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    // 自定义方法，根据uniquekey查找新闻
    Optional<News> findByUniquekey(String uniquekey);

}