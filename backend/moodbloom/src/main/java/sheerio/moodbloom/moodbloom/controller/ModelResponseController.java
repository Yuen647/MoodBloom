package sheerio.moodbloom.moodbloom.controller;

import sheerio.moodbloom.moodbloom.service.ModelResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/model-response")
public class ModelResponseController {

    @Autowired
    private ModelResponseService modelResponseService;

    @GetMapping("/send/{moodRecordId}")
    public String sendModelResponse(@PathVariable Integer moodRecordId) {
        return modelResponseService.sendToModelAndSaveResponse(moodRecordId);
    }
}
