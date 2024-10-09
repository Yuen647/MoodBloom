package sheerio.moodbloom.moodbloom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sheerio.moodbloom.moodbloom.dao.model.SoupQuotes;
import sheerio.moodbloom.moodbloom.dao.Interface.SoupQuotesRepository;
import sheerio.moodbloom.moodbloom.service.SoupQuotesService;

import java.util.List;

@RestController
@RequestMapping("/api/soup-quotes")
public class SoupQuotesController {

    @Autowired
    private SoupQuotesService soupQuotesService;

    @Autowired
    private SoupQuotesRepository soupQuotesRepository;

    // 获取并保存心灵鸡汤语录
    @PostMapping("/fetch")
    public String fetchAndSaveQuote() {
        soupQuotesService.fetchAndSaveQuote();
        return "心灵鸡汤语录已成功获取并保存!";
    }

    // 获取数据库中所有心灵鸡汤语录
    @GetMapping("/all")
    public List<SoupQuotes> getAllQuotes() {
        return soupQuotesRepository.findAll();
    }

    // 根据ID获取特定的心灵鸡汤语录
    @GetMapping("/{id}")
    public SoupQuotes getQuoteById(@PathVariable Integer id) {
        return soupQuotesRepository.findById(id).orElse(null);
    }

    // 获取最新的心灵鸡汤语录
    @GetMapping("/latest")
    public SoupQuotes getLatestQuote() {
        return soupQuotesRepository.findLatestQuote();
    }
}
