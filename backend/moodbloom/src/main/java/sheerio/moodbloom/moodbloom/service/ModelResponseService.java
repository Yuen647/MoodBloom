package sheerio.moodbloom.moodbloom.service;

import sheerio.moodbloom.moodbloom.dao.model.ModelResponse;
import sheerio.moodbloom.moodbloom.dao.model.MoodRecord;
import sheerio.moodbloom.moodbloom.dao.Interface.ModelResponseRepository;
import sheerio.moodbloom.moodbloom.dao.Interface.MoodRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelResponseService {

    @Autowired
    private MoodRecordRepository moodRecordRepository;

    @Autowired
    private ModelResponseRepository modelResponseRepository;

    @Autowired
    private RestTemplate restTemplate;

    // 从 application.yml 文件中注入配置
    @Value("${chatglm-api.url}")
    private String apiUrl;

    @Value("${chatglm-api.key}")
    private String apiKey;

    @Value("${chatglm-api.model}")
    private String modelName;

    private List<Map<String, String>> chatHistory = new ArrayList<>();

    // 调用 AI 模型生成回复并保存
    public String sendToModelAndSaveResponse(Integer moodRecordId) {
        // 获取 MoodRecord 中的 mood_text
        MoodRecord moodRecord = moodRecordRepository.findById(moodRecordId)
                .orElseThrow(() -> new RuntimeException("MoodRecord not found"));
        String moodText = moodRecord.getMoodText();

        // 创建用户输入消息
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", moodText);

        // 添加到聊天历史记录
        chatHistory.add(userMessage);

        // 准备请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("messages", chatHistory);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // 调用 API
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();

                // 解析响应
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseObject = objectMapper.readTree(responseBody);
                JsonNode choices = responseObject.path("choices");

                if (choices.isArray() && choices.size() > 0) {
                    JsonNode message = choices.get(0).path("message");
                    String assistantMessage = message.path("content").asText();

                    // 将助手的消息添加到聊天历史
                    Map<String, String> assistantResponse = new HashMap<>();
                    assistantResponse.put("role", "assistant");
                    assistantResponse.put("content", assistantMessage);
                    chatHistory.add(assistantResponse);

                    // 创建 ModelResponse 实体并保存到数据库
                    ModelResponse modelResponse = new ModelResponse();
                    modelResponse.setMoodRecord(moodRecord);
                    modelResponse.setResponseText(assistantMessage);
                    modelResponse.setResponseTime(LocalDateTime.now());

                    modelResponseRepository.save(modelResponse);

                    return assistantMessage;
                } else {
                    return "响应解析失败，未找到消息内容。";
                }
            } else {
                return "API 请求失败，状态码：" + response.getStatusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "调用 API 时发生错误：" + e.getMessage();
        }
    }

    // 根据 MoodRecord ID 获取对应的 AI 回复
    public ModelResponse getResponseByMoodRecordId(Integer moodRecordId) {
        return modelResponseRepository.findByMoodRecordId(moodRecordId);
    }

    // 获取所有 ModelResponse 记录
    public List<ModelResponse> getAllModelResponses() {
        return modelResponseRepository.findAll();
    }

    // 根据回复 ID 删除 AI 回复
    public void deleteModelResponseById(Integer responseId) {
        modelResponseRepository.deleteById(responseId);
    }
}
