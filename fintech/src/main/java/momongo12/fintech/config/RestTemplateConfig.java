package momongo12.fintech.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import momongo12.fintech.services.exceptions.WeatherApiErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Momongo12
 * @version 1.0
 */
@Configuration
public class RestTemplateConfig {

    @Value("${app.weather-api.token}")
    private String weatherApiToken;

    public static final String ROOT_URI_FOR_WEATHER_API = "https://api.weatherapi.com/v1";

    @Bean
    public RestTemplate restTemplateForWeatherApi() {
        return new RestTemplateBuilder()
                .rootUri(ROOT_URI_FOR_WEATHER_API)
                .defaultHeader("key", weatherApiToken)
                .errorHandler(new WeatherApiErrorHandler(objectMapper()))
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
