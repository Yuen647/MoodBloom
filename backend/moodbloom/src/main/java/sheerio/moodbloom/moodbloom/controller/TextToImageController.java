package sheerio.moodbloom.moodbloom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sheerio.moodbloom.moodbloom.service.TextToImageService;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/txt2img")
public class TextToImageController {

    private final TextToImageService textToImageService;

    public TextToImageController(TextToImageService textToImageService) {
        this.textToImageService = textToImageService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateImage(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().body("Text cannot be null or empty");
        }

        try {
            // Step 1: 生成图片的UID
            String uid = textToImageService.generateImageUid(text);

            // Step 2: 使用带轮询机制的方法获取图片链接
            String imageLink = textToImageService.getImageLinkWithPolling(uid);

            // 返回图片链接
            return ResponseEntity.ok(imageLink);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(500).body("Error generating image: " + e.getMessage());
        }
    }

}
