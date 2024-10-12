package sheerio.moodbloom.moodbloom.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sheerio.moodbloom.moodbloom.dao.model.Location;
import sheerio.moodbloom.moodbloom.dao.model.WeatherInfo;
import sheerio.moodbloom.moodbloom.dao.Interface.WeatherInfoRepository;

import java.io.IOException;

@Service
public class WeatherService {

    @Value("${weather-api.key}") // 从 application.yml 中读取 API key
    private String apiKey;

    @Value("${weather-api.base-url}") // 从 application.yml 中读取 API URL
    private String baseUrl;

    private final OkHttpClient client = new OkHttpClient().newBuilder().build();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson 用于解析 JSON

    @Autowired
    private WeatherInfoRepository weatherInfoRepository;

    // 获取并保存天气信息
    public WeatherInfo fetchWeatherInfo(Location location) throws IOException {
        String lonlat = location.getLonlat();  // 获取经纬度

        // 构建API请求URL
        String url = baseUrl + "?lonlat=" + lonlat;

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-APISpace-Token", apiKey)
                .build();

        // 发送请求
        Response response = client.newCall(request).execute();

        if (response.isSuccessful() && response.body() != null) {
            String responseBody = response.body().string();

            // 使用 Jackson 解析 JSON 响应
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode resultNode = rootNode.path("result").path("realtime");

            // 提取 "text", "temp", "detail" 字段
            String text = resultNode.path("text").asText();
            Double temp = resultNode.path("temp").asDouble();
            String detail = resultNode.path("detail").asText();

            // 创建并保存 WeatherInfo
            WeatherInfo weatherInfo = new WeatherInfo();
            weatherInfo.setLocation(location);
            weatherInfo.setText(text);
            weatherInfo.setTemp(temp);
            weatherInfo.setDetail(detail);

            return weatherInfoRepository.save(weatherInfo);
        } else {
            throw new IOException("Failed to fetch weather info: " + response.message());
        }
    }
}
