package sheerio.moodbloom.moodbloom.dao.Interface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sheerio.moodbloom.moodbloom.dao.model.SoupQuotes;

public interface SoupQuotesRepository extends JpaRepository<SoupQuotes, Integer> {
    //获取最新的语录
    @Query(value = "SELECT * FROM soup_quotes ORDER BY record_date DESC LIMIT 1", nativeQuery = true)
    SoupQuotes findLatestQuote();
}

