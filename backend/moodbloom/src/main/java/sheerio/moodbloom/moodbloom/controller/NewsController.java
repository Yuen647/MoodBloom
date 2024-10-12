package sheerio.moodbloom.moodbloom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sheerio.moodbloom.moodbloom.dao.model.News;
import sheerio.moodbloom.moodbloom.service.NewsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;


    //从API获取今日7条新闻并保存到数据库
    @GetMapping("/fetch")
    public ResponseEntity<String> fetchNews() {
        try {
            newsService.fetchAndSaveNews();
            return ResponseEntity.ok("新闻数据已成功获取并保存到数据库。");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取新闻数据时发生错误：" + e.getMessage());
        }
    }

    // 获取最新的7条新闻
    @GetMapping("/latest")
    public ResponseEntity<List<News>> getLatestNews() {
        try {
            List<News> latestNews = newsService.getLatestNews();
            return ResponseEntity.ok(latestNews);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}

