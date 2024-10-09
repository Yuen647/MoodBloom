package sheerio.moodbloom.moodbloom.dao.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
public class UploadImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 存储图片的文件路径或URL
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    // 图片上传时间
    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

    // 关联到 MoodRecord 实体，表示 mood_record_id 是外键
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_record_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties("images")
    private MoodRecord moodRecord;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public MoodRecord getMoodRecord() {
        return moodRecord;
    }

    public void setMoodRecord(MoodRecord moodRecord) {
        this.moodRecord = moodRecord;
    }


}
