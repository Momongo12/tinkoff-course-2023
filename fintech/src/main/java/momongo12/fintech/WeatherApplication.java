package momongo12.fintech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Momongo12
 * @version 1.1
 */
@SpringBootApplication
@EnableScheduling
public class WeatherApplication {

    public static void main(String[] args) {
         SpringApplication.run(WeatherApplication.class, args);
    }
}
