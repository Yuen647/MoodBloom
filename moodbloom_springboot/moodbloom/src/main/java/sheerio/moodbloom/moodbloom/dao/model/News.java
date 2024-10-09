package sheerio.moodbloom.moodbloom.dao.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uniquekey;  // 新闻的唯一标识符

    @Column(nullable = false)
    private String title;      // 新闻标题

    @Column(nullable = false)
    private String url;        // 新闻链接

    private String category;   // 新闻类别

    private String authorName; // 作者名称

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date date;         // 新闻发布时间

    private String thumbnailPicS;   // 缩略图1的URL

    private String thumbnailPicS02; // 缩略图2的URL

    private String thumbnailPicS03; // 缩略图3的URL

    @Column(nullable = false)
    private boolean isContent;  // 是否有详细内容

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(); // 记录创建时间

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date(); // 记录更新时间

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getThumbnailPicS() {
        return thumbnailPicS;
    }

    public void setThumbnailPicS(String thumbnailPicS) {
        this.thumbnailPicS = thumbnailPicS;
    }

    public String getThumbnailPicS02() {
        return thumbnailPicS02;
    }

    public void setThumbnailPicS02(String thumbnailPicS02) {
        this.thumbnailPicS02 = thumbnailPicS02;
    }

    public String getThumbnailPicS03() {
        return thumbnailPicS03;
    }

    public void setThumbnailPicS03(String thumbnailPicS03) {
        this.thumbnailPicS03 = thumbnailPicS03;
    }

    public boolean getIsContent() {
        return isContent;
    }

    public void setIsContent(boolean isContent) {
        this.isContent = isContent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

