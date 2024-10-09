package sheerio.moodbloom.moodbloom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sheerio.moodbloom.moodbloom.dao.model.MoodRecord;
import sheerio.moodbloom.moodbloom.dao.model.ModelResponse;
import sheerio.moodbloom.moodbloom.dao.model.UploadImage;
import sheerio.moodbloom.moodbloom.service.MoodRecordService;
import sheerio.moodbloom.moodbloom.service.ModelResponseService;
import sheerio.moodbloom.moodbloom.service.UploadImageService;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/mood")
public class MoodRecordController {

    @Autowired
    private MoodRecordService moodRecordService;

    @Autowired
    private ModelResponseService modelResponseService;

    @Autowired
    private UploadImageService uploadImageService;

    // 新增心情记录并支持图片上传
    @PostMapping("/add")
    public ResponseEntity<?> addMoodRecord(@RequestParam("userId") Integer userId,
                                           @RequestParam(value = "moodText", required = false) String moodText,
                                           @RequestParam(value = "images", required = false) MultipartFile[] images) {
        try {
            MoodRecord moodRecord = new MoodRecord();
            moodRecord.setMoodText(moodText);

            // 添加心情记录，并处理图片上传
            MoodRecord newMoodRecord = moodRecordService.addMoodRecord(moodRecord, userId, images);

            // 调用 AI 模型生成回复
            String aiResponse = modelResponseService.sendToModelAndSaveResponse(newMoodRecord.getId());

            // 返回心情记录和 AI 回复
            Map<String, Object> result = new HashMap<>();
            result.put("moodRecord", newMoodRecord);
            result.put("aiResponse", aiResponse);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


    // 查询指定用户的所有心情记录及其对应的AI回复
    @GetMapping("/all")
    public ResponseEntity<?> getAllMoodRecords(@RequestParam("userId") Integer userId,
                                               @RequestParam(value = "view", defaultValue = "both") String view) {
        try {
            List<MoodRecord> moodRecords = moodRecordService.getMoodRecordsByUserId(userId);
            Map<String, Object> result = new HashMap<>();

            if (view.equals("mood")) {
                result.put("moodRecords", moodRecords);
            } else {
                Map<Integer, String> aiResponses = new HashMap<>();
                for (MoodRecord moodRecord : moodRecords) {
                    ModelResponse modelResponse = modelResponseService.getResponseByMoodRecordId(moodRecord.getId());
                    String aiResponse = modelResponse != null ? modelResponse.getResponseText() : "AI 回复不存在";
                    aiResponses.put(moodRecord.getId(), aiResponse);
                }
                result.put("moodRecords", moodRecords);
                result.put("aiResponses", aiResponses);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/byDate")
    public ResponseEntity<?> getMoodRecordsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "includeAIResponses", required = false) Boolean includeAIResponses) {
        try {
            // 查询特定日期和用户的心情记录
            List<MoodRecord> moodRecords = moodRecordService.getMoodRecordsByDateAndUserId(date, userId);

            // 创建一个新的列表，用来存放心情记录、AI回复和图片URLs
            List<Map<String, Object>> moodRecordsWithDetails = new ArrayList<>();

            for (MoodRecord moodRecord : moodRecords) {
                Map<String, Object> moodDetails = new HashMap<>();
                moodDetails.put("moodRecord", moodRecord);

                // 获取与心情记录关联的图片URLs
                List<String> imageUrls = new ArrayList<>();
                if (moodRecord.getImages() != null) {
                    for (UploadImage image : moodRecord.getImages()) {
                        imageUrls.add(image.getImageUrl());
                    }
                }
                moodDetails.put("imageUrls", imageUrls);

                // 获取对应的AI回复
                if (Boolean.TRUE.equals(includeAIResponses)) {
                    ModelResponse modelResponse = modelResponseService.getResponseByMoodRecordId(moodRecord.getId());
                    String aiResponse = modelResponse != null ? modelResponse.getResponseText() : "AI 回复不存在";
                    moodDetails.put("aiResponse", aiResponse);
                }

                moodRecordsWithDetails.add(moodDetails);
            }

            // 返回心情记录和AI回复及图片URLs
            return ResponseEntity.ok(moodRecordsWithDetails);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


    // 根据用户ID和记录ID删除心情记录
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMoodRecord(@PathVariable Integer id, @RequestParam("userId") Integer userId) {
        try {
            boolean isDeleted = moodRecordService.deleteMoodRecordByIdAndUserId(id, userId);
            if (isDeleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(403).body("Not authorized to delete this record.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

    // 获取指定心情记录的图片
    @GetMapping("/images/{moodRecordId}")
    public ResponseEntity<?> getImagesByMoodRecordId(@PathVariable Integer moodRecordId) {
        try {
            // 获取与心情记录关联的图片
            List<UploadImage> images = uploadImageService.getImagesByMoodRecordId(moodRecordId);
            if (images.isEmpty()) {
                return ResponseEntity.ok("No images found for this mood record.");
            }

            // 返回图片的 URL 列表
            List<String> imageUrls = new ArrayList<>();
            for (UploadImage image : images) {
                imageUrls.add(image.getImageUrl());
            }

            return ResponseEntity.ok(imageUrls);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
