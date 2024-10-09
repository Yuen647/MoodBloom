package sheerio.moodbloom.moodbloom.dao.Interface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sheerio.moodbloom.moodbloom.dao.model.ModelResponse;

@Repository
public interface ModelResponseRepository extends JpaRepository<ModelResponse,Integer> {
    ModelResponse findByMoodRecordId(Integer moodRecordId);

}
