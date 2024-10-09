package sheerio.moodbloom.moodbloom.dao.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mood_analysis")
public class MoodAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sentiment", nullable = false)
    private Byte sentiment;

    @Column(name = "confidence", precision = 5, scale = 4)
    private BigDecimal confidence;

    @Column(name = "positive_prob", precision = 5, scale = 4)
    private BigDecimal positiveProb;

    @Column(name = "negative_prob", precision = 5, scale = 4)
    private BigDecimal negativeProb;



    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Byte getSentiment() {
        return sentiment;
    }

    public void setSentiment(Byte sentiment) {
        this.sentiment = sentiment;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public BigDecimal getPositiveProb() {
        return positiveProb;
    }

    public void setPositiveProb(BigDecimal positiveProb) {
        this.positiveProb = positiveProb;
    }

    public BigDecimal getNegativeProb() {
        return negativeProb;
    }

    public void setNegativeProb(BigDecimal negativeProb) {
        this.negativeProb = negativeProb;
    }

}
