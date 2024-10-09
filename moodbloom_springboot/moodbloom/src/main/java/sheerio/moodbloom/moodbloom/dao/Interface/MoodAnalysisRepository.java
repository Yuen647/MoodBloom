package sheerio.moodbloom.moodbloom.dao.Interface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sheerio.moodbloom.moodbloom.dao.model.MoodAnalysis;

@Repository
public interface MoodAnalysisRepository extends JpaRepository<MoodAnalysis, Integer> {

}
