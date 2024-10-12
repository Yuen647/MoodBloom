package sheerio.moodbloom.moodbloom.dao.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mood_record")
public class MoodRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "mood_text", nullable = false)
    private String moodText;

    @Column(name = "record_time", nullable = false)
    private LocalDateTime recordTime;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = true)
    private Location location;

    @ManyToOne
    @JoinColumn(name = "weather_id", referencedColumnName = "id", nullable = true)
    private WeatherInfo weatherInfo;

    @ManyToOne
    @JoinColumn(name = "analysis_id", referencedColumnName = "id", nullable = true)
    private MoodAnalysis moodAnalysis;

    // 关联到 User 实体，表示 user_id 是外键
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;


    @OneToMany(mappedBy = "moodRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("moodRecord") // 防止递归引用
    private List<UploadImage> images = new ArrayList<>();


    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMoodText() {
        return moodText;
    }

    public void setMoodText(String moodText) {
        this.moodText = moodText;
    }

    public LocalDateTime getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(LocalDateTime recordTime) {
        this.recordTime = recordTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public WeatherInfo getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(WeatherInfo weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public MoodAnalysis getMoodAnalysis() {
        return moodAnalysis;
    }

    public void setMoodAnalysis(MoodAnalysis moodAnalysis) {
        this.moodAnalysis = moodAnalysis;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<UploadImage> getImages() {
        return images;
    }

    public void setImages(List<UploadImage> images) {
        this.images = images;
    }

}
