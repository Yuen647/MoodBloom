package sheerio.moodbloom.moodbloom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sheerio.moodbloom.moodbloom.dao.model.SoupQuotes;
import sheerio.moodbloom.moodbloom.dao.Interface.SoupQuotesRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class SoupQuotesService {

    @Autowired
    private SoupQuotesRepository soupQuotesRepository;

    @Autowired
    private RestTemplate restTemplate;

    // 从 application.yml 读取 API 的相关配置信息
    @Value("${soup-quotes.api.url}")
    private String apiUrl;

    @Value("${soup-quotes.api.key}")
    private String apiKey;

    // 调用 API 并保存鸡汤语录
    public void fetchAndSaveQuote() {
        // 构建 API URL
        String apiRequestUrl = String.format("%s?key=%s", apiUrl, apiKey);

        // 调用 API
        Map<String, Object> response = restTemplate.getForObject(apiRequestUrl, Map.class);

        // 解析 API 响应
        if (response != null && "success".equals(response.get("reason"))) {
            Map<String, String> result = (Map<String, String>) response.get("result");
            String quoteText = result.get("text");

            // 创建新的 SoupQuotes 实体
            SoupQuotes soupQuote = new SoupQuotes();
            soupQuote.setText(quoteText);
            soupQuote.setRecordDate(LocalDateTime.now());

            // 保存到数据库
            soupQuotesRepository.save(soupQuote);
        }
    }
}
