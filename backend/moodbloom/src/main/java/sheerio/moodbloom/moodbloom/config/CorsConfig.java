package sheerio.moodbloom.moodbloom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOrigins(
                        "http://localhost:8080",
                        "https://124.222.156.13", // 你服务器的公网IP
                        "https://yuen.club" // 你服务器的域名
                ) // 允许多个来源的跨域请求
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
                .allowedHeaders("*") // 允许所有头部
                .allowCredentials(true) // 允许携带凭证（如Cookies）
                .maxAge(3600); // 预检请求的缓存时间，单位为秒
    }
}

