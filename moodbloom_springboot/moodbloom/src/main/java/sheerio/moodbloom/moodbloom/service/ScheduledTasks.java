package sheerio.moodbloom.moodbloom.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private SoupQuotesService soupQuotesService;

    // 使用cron表达式配置任务每小时执行一次，表达式："0 0 * * * ?"
    @Scheduled(cron = "0 0 * * * ?")
    public void fetchSoupQuote() {
        // 调用SoupQuotesService的API获取和保存数据的方法
        soupQuotesService.fetchAndSaveQuote();
    }
}
