package sheerio.moodbloom.moodbloom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoodbloomApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoodbloomApplication.class, args);
	}

}
