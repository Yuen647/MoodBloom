package sheerio.moodbloom.moodbloom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sheerio.moodbloom.moodbloom.dao.Interface.UploadImageRepository;
import sheerio.moodbloom.moodbloom.dao.model.UploadImage;

import java.util.List;

@Service
public class UploadImageService {

    @Autowired
    private UploadImageRepository uploadImageRepository;

    // 根据心情记录ID获取图片
    public List<UploadImage> getImagesByMoodRecordId(Integer moodRecordId) {
        return uploadImageRepository.findByMoodRecordId(moodRecordId);
    }
}
