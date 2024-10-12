package sheerio.moodbloom.moodbloom.dao.model;

import jakarta.persistence.*;

@Entity
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "location_msg", length = 255)
    private String location_msg;

    @Column(name = "lonlat", length = 255)
    private String lonlat;

    // 添加外键关联 user_id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation_msg() {
        return location_msg;
    }

    public void setLocation_msg(String location_msg) {
        this.location_msg = location_msg;
    }

    public String getLonlat() {
        return lonlat;
    }

    public void setLonlat(String lonlat) {
        this.lonlat = lonlat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
