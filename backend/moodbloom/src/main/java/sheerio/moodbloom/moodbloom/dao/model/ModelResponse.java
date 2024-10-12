package sheerio.moodbloom.moodbloom.dao.model;
import java.time.LocalDateTime;
import jakarta.persistence.*;


@Entity
@Table(name = "model_response")
public class ModelResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer responseId;

    @ManyToOne
    @JoinColumn(name = "input_id", referencedColumnName = "id",nullable = false)
    private MoodRecord moodRecord;

    @Column(name = "response_text", columnDefinition = "TEXT")
    private String responseText;

    @Column(name = "response_time", nullable = false)
    private LocalDateTime responseTime;

    public Integer getResponseId() {
        return responseId;
    }

    public void setResponseId(Integer responseId) {
        this.responseId = responseId;
    }

    public MoodRecord getMoodRecord() {
        return moodRecord;
    }

    public void setMoodRecord(MoodRecord moodRecord) {
        this.moodRecord = moodRecord;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public LocalDateTime getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(LocalDateTime responseTime) {
        this.responseTime = responseTime;
    }


}
