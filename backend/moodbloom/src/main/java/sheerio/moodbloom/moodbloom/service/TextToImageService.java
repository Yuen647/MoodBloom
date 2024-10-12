package sheerio.moodbloom.moodbloom.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class TextToImageService {

    private final OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();  // 创建 Jackson ObjectMapper 实例

    @Value("${pic.space.token}")
    private String apiToken;

    @Value("${pic.txt2img.url}")
    private String txt2ImgUrl;

    @Value("${pic.query.image.url}")
    private String queryImageUrl;

    // Step 1: 调用API生成图片，返回uid
    public String generateImageUid(String text) throws IOException {
        // 构造请求体，包含用户提供的文本
        String jsonBody = String.format("{\"task\":\"txt2img.sd\",\"params\":{\"model\":\"anime\",\"text\":\"%s\"}}", text);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        // 构造请求，发送 POST 请求生成图片
        Request request = new Request.Builder()
                .url(txt2ImgUrl)
                .post(body)
                .addHeader("X-APISpace-Token", apiToken)
                .addHeader("Content-Type", "application/json")
                .build();

        // 执行请求并获取响应
        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Error generating image UID: " + response);
        }

        // 解析 JSON 响应并提取 UID
        String responseBody = response.body() != null ? response.body().string() : null;
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("data").get("uid").asText();  // 返回生成的 UID
    }

    // Step 2: 根据uid查询图片链接
    public String getImageLink(String uid) throws IOException {
        String jsonBody = String.format("{\"uid\":\"%s\"}", uid);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Request request = new Request.Builder()
                .url(queryImageUrl)
                .post(body)
                .addHeader("X-APISpace-Token", apiToken)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error querying image link: " + response);
            }

            // 读取并保存响应体
            String responseBody = response.body() != null ? response.body().string() : null;
            System.out.println("Image Link Response: " + responseBody);

            return responseBody;
        }
    }


    public String getImageLinkWithPolling(String uid) throws IOException, InterruptedException {
        int retries = 100;  // 最大重试次数
        int interval = 2000;  // 每次重试间隔 2 秒

        for (int i = 0; i < retries; i++) {
            String responseBody = getImageLink(uid);

            // 打印调试信息，检查完整的 API 响应
            System.out.println("Full API Response: " + responseBody);

            if (responseBody == null || responseBody.isEmpty()) {
                System.out.println("Empty or null response body.");
                throw new IOException("Received empty response body from API.");
            }

            // 解析返回的JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // 检查是否存在 "data" 节点
            JsonNode dataNode = jsonNode.get("data");
            if (dataNode == null) {
                System.out.println("No 'data' field in the response.");
                throw new IOException("No 'data' field in the response");
            }

            // 获取 "status" 节点
            String status = dataNode.get("status").asText();
            System.out.println("Image generation status: " + status);

            // 如果状态为 "finished"，获取图片链接
            if ("finished".equals(status)) {
                String cdn = dataNode.get("cdn").asText();  // 获取图片链接
                if (cdn != null && !cdn.isEmpty()) {
                    return cdn;  // 返回图片链接
                } else {
                    throw new IOException("Image link (cdn) is empty or null.");
                }
            }

            // 如果状态不是 "finished"，等待一段时间后重试
            System.out.println("Image generation in progress, retrying in " + (interval / 1000) + " seconds...");
            Thread.sleep(interval);  // 等待后重试
        }

        throw new IOException("Failed to get image link after retries");
    }





}
