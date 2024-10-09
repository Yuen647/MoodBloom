package sheerio.moodbloom.moodbloom.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sheerio.moodbloom.moodbloom.dao.Interface.NewsRepository;
import sheerio.moodbloom.moodbloom.dao.model.News;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private RestTemplate restTemplate;

    // 从 application.yml 读取 API 的相关配置信息
    @Value("${news.api.key}")
    private String apiKey;

    @Value("${news.api.url}")
    private String apiUrl;

    @Value("${news.api.type}")
    private String apiType;

    @Value("${news.api.page}")
    private int apiPage;

    @Value("${news.api.page_size}")
    private int apiPageSize;

    @Value("${news.api.is_filter}")
    private int isFilter;

    // 从 API 获取新闻并保存到数据库
    public void fetchAndSaveNews() {
        // 构建 API URL
        String apiRequestUrl = String.format("%s?type=%s&page=%d&page_size=%d&is_filter=%d&key=%s",
                apiUrl, apiType, apiPage, apiPageSize, isFilter, apiKey);

        // 调用 API 获取新闻数据
        String response = restTemplate.getForObject(apiRequestUrl, String.class);

        // 解析返回的 JSON 数据
        JSONObject jsonObject = new JSONObject(response);
        if (!jsonObject.has("result")) {
            // 错误处理：API 返回结果不包含新闻数据
            return;
        }

        JSONArray newsArray = jsonObject.getJSONObject("result").getJSONArray("data");

        // 遍历新闻数据并保存到数据库
        for (int i = 0; i < newsArray.length(); i++) {
            JSONObject newsItem = newsArray.getJSONObject(i);

            // 检查新闻是否已经存在于数据库中
            Optional<News> existingNews = newsRepository.findByUniquekey(newsItem.getString("uniquekey"));

            if (!existingNews.isPresent()) {
                // 如果新闻不存在，保存新新闻
                News news = new News();
                news.setUniquekey(newsItem.getString("uniquekey"));
                news.setTitle(newsItem.getString("title"));
                news.setUrl(newsItem.getString("url"));
                news.setCategory(newsItem.optString("category", "未分类"));
                news.setAuthorName(newsItem.optString("author_name", "未知"));
                news.setDate(new Date());  // 使用当前时间，或根据需求修改为 API 返回的时间
                news.setThumbnailPicS(newsItem.optString("thumbnail_pic_s", ""));
                news.setThumbnailPicS02(newsItem.optString("thumbnail_pic_s02", ""));
                news.setThumbnailPicS03(newsItem.optString("thumbnail_pic_s03", ""));
                news.setIsContent(newsItem.optInt("is_content", 0) == 1);

                // 保存新闻到数据库
                newsRepository.save(news);
            }
        }
    }

    // 获取最新的7条新闻
    public List<News> getLatestNews() {
        // 按照日期降序排列，获取前7条记录
        return newsRepository.findAll(
                PageRequest.of(0, 7, Sort.by(Sort.Direction.DESC, "date"))).getContent();
    }

}
