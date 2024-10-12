package sheerio.moodbloom.moodbloom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sheerio.moodbloom.moodbloom.config.TextRequest;
import sheerio.moodbloom.moodbloom.dao.model.MoodAnalysis;
import sheerio.moodbloom.moodbloom.service.MoodAnalysisService;

@RestController
@RequestMapping("/api/mood")
public class MoodAnalysisController {
    private final MoodAnalysisService moodAnalysisService;

    public MoodAnalysisController(MoodAnalysisService moodAnalysisService) {
        this.moodAnalysisService = moodAnalysisService;
    }

    // 修改为使用 @RequestBody 接收 JSON 格式的请求
    @PostMapping("/analyze")
    public ResponseEntity<MoodAnalysis> analyzeMood(@RequestBody TextRequest textRequest) {
        try {
            MoodAnalysis result = moodAnalysisService.analyzeMood(textRequest.getText());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}

