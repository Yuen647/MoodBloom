package sheerio.moodbloom.moodbloom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/speech")
    public String speechRecognition() {
        return "speechRecognition";  // 对应 templates/speechRecognition.html
    }
}
