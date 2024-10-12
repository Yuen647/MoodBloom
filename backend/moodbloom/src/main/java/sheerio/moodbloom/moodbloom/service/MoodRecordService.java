package sheerio.moodbloom.moodbloom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sheerio.moodbloom.moodbloom.dao.Interface.MoodRecordRepository;
import sheerio.moodbloom.moodbloom.dao.Interface.UploadImageRepository;
import sheerio.moodbloom.moodbloom.dao.model.Location;
import sheerio.moodbloom.moodbloom.dao.model.MoodAnalysis;
import sheerio.moodbloom.moodbloom.dao.model.MoodRecord;
import sheerio.moodbloom.moodbloom.dao.model.UploadImage;
import sheerio.moodbloom.moodbloom.dao.model.User;
import sheerio.moodbloom.moodbloom.dao.model.WeatherInfo;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class MoodRecordService {

    @Autowired
    private MoodRecordRepository moodRecordRepository;

    @Autowired
    private UploadImageRepository uploadImageRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private MoodAnalysisService moodAnalysisService;

    // 从配置文件中读取图片存储路径
    @Value("${file.upload-dir}")
    private String uploadDir;

    // 创建心情记录时自动获取位置信息、天气信息并调用情感分析，同时处理多张图片上传
    public MoodRecord addMoodRecord(MoodRecord moodRecord, Integer userId, MultipartFile[] images) throws Exception {
        // 关联用户ID到心情记录
        User user = new User();
        user.setId(userId);
        moodRecord.setUser(user);

        // 检查是否有提供位置信息
        Location location = moodRecord.getLocation();

        // 如果没有提供位置信息，从数据库中获取最近的位置信息
        if (location == null) {
            Optional<Location> latestLocation = locationService.getLatestLocation();
            if (latestLocation.isPresent()) {
                location = latestLocation.get();
            } else {
                throw new Exception("No location information available.");
            }
        }

        // 获取天气信息
        WeatherInfo weatherInfo = weatherService.fetchWeatherInfo(location);

        // 将位置信息和天气信息关联到心情记录
        moodRecord.setLocation(location);
        moodRecord.setWeatherInfo(weatherInfo);

        // 如果 moodText 为空，则自动填充占位符
        if (moodRecord.getMoodText() == null || moodRecord.getMoodText().trim().isEmpty()) {
            moodRecord.setMoodText("拍拍照~");
        }

        // 调用情感分析服务获取情感分析结果
        MoodAnalysis moodAnalysis = moodAnalysisService.analyzeMood(moodRecord.getMoodText());

        // 将情感分析结果关联到心情记录
        moodRecord.setMoodAnalysis(moodAnalysis);

        // 设置记录时间为当前时间
        moodRecord.setRecordTime(LocalDateTime.now());

        // 保存心情记录
        MoodRecord savedMoodRecord = moodRecordRepository.save(moodRecord);

        // 如果有上传的图片，逐一保存图片
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = saveImage(image);  // 保存图片并返回其URL
                    UploadImage uploadImage = new UploadImage();
                    uploadImage.setImageUrl(imageUrl);
                    uploadImage.setUploadTime(LocalDateTime.now());
                    uploadImage.setMoodRecord(savedMoodRecord);

                    // 保存图片记录到数据库
                    uploadImageRepository.save(uploadImage);
                    savedMoodRecord.getImages().add(uploadImage);  // 将图片关联到心情记录
                    System.out.println("Image URL saved to database: " + imageUrl);
                }
            }
        }

        // 返回保存的心情记录
        return savedMoodRecord;
    }

    // 保存图片到配置的上传目录，并返回图片的 URL 路径
    public String saveImage(MultipartFile image) throws IOException {
        // 获取文件名
        String fileName = image.getOriginalFilename();
        // 生成保存图片的完整路径
        String imagePath = uploadDir + "/" + fileName;
        File file = new File(imagePath);

        // 创建目录如果不存在
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
            System.out.println("Directory created: " + file.getParentFile().getAbsolutePath());
        }

        // 将文件保存到指定路径
        image.transferTo(file);
        System.out.println("Image saved to: " + imagePath);

        // 返回浏览器可以访问的 URL 路径
        return "/images/" + fileName;
    }


    // 获取指定用户的所有心情记录
    public List<MoodRecord> getMoodRecordsByUserId(Integer userId) {
        return moodRecordRepository.findByUserId(userId);
    }

    // 根据用户ID和记录ID删除心情记录
    public boolean deleteMoodRecordByIdAndUserId(Integer recordId, Integer userId) throws Exception {
        MoodRecord record = moodRecordRepository.findByIdAndUserId(recordId, userId);
        if (record != null) {
            moodRecordRepository.delete(record);
            return true;
        } else {
            throw new Exception("Mood record not found or user not authorized.");
        }
    }

    // 按日期查询指定用户的心情记录
    public List<MoodRecord> getMoodRecordsByDateAndUserId(LocalDate date, Integer userId) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return moodRecordRepository.findByUserIdAndRecordTimeBetween(userId, startOfDay, endOfDay);
    }
}
