package sheerio.moodbloom.moodbloom.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sheerio.moodbloom.moodbloom.dao.Interface.MoodAnalysisRepository;
import sheerio.moodbloom.moodbloom.dao.model.MoodAnalysis;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class MoodAnalysisService {

    private final RestTemplate restTemplate;
    private final MoodAnalysisRepository moodAnalysisRepository;
    private final ObjectMapper objectMapper;

    @Value("${baidu.nlp.api-url}")
    private String apiUrl;

    @Value("${baidu.nlp.access-token}")
    private String accessToken;

    public MoodAnalysisService(RestTemplate restTemplate, MoodAnalysisRepository moodAnalysisRepository, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.moodAnalysisRepository = moodAnalysisRepository;
        this.objectMapper = objectMapper;
    }

    public MoodAnalysis analyzeMood(String text) throws Exception {
        // 构建请求
        String requestUrl = apiUrl + "?access_token=" + accessToken;
        String requestBody = "{\"text\":\"" + text + "\"}";

        // 发送请求
        String response = restTemplate.postForObject(requestUrl, requestBody, String.class);

        // 解析响应
        JsonNode root = objectMapper.readTree(response);
        JsonNode items = root.path("items").get(0);

        // 获取情感分析结果
        Byte sentiment = (byte) items.path("sentiment").asInt();
        BigDecimal confidence = BigDecimal.valueOf(items.path("confidence").asDouble());
        BigDecimal positiveProb = BigDecimal.valueOf(items.path("positive_prob").asDouble());
        BigDecimal negativeProb = BigDecimal.valueOf(items.path("negative_prob").asDouble());

        // 保存结果到数据库
        MoodAnalysis moodAnalysis = new MoodAnalysis();
        moodAnalysis.setSentiment(sentiment);
        moodAnalysis.setConfidence(confidence);
        moodAnalysis.setPositiveProb(positiveProb);
        moodAnalysis.setNegativeProb(negativeProb);

        return moodAnalysisRepository.save(moodAnalysis);
    }
}

