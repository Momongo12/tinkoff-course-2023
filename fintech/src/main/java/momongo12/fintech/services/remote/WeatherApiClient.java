package momongo12.fintech.services.remote;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import momongo12.fintech.api.controllers.exceptions.InternalServerErrorException;
import momongo12.fintech.api.dto.WeatherApiResponse;
import momongo12.fintech.services.exceptions.WeatherApiTokenKeyException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Optional;

/**
 * @author Momongo12
 * @version 1.0
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class WeatherApiClient {

    final RestTemplate restTemplate;
    public static final String GET_CURRENT_TEMPERATURE = "/current.json";

    public WeatherApiClient(@Qualifier("restTemplateForWeatherApi") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RateLimiter(name = "weatherApiRateLimiter")
    public Optional<WeatherApiResponse> getCurrentWeather(String regionName){
        if (regionName == null || regionName.isEmpty()) {
            throw new IllegalArgumentException("Incorrect region name");
        }

        try {
            ResponseEntity<WeatherApiResponse> response = restTemplate.
                    exchange(
                            createUrlWithRegionName(regionName),
                            HttpMethod.GET,
                            null,
                            WeatherApiResponse.class);

            return Optional.ofNullable(response.getBody());
        }catch (WeatherApiTokenKeyException | InternalServerErrorException ex) {
            log.error(ex.getMessage());
        }

        return Optional.empty();
    }

    private String createUrlWithRegionName(String regionName) {
        return UriComponentsBuilder.newInstance()
                .path(GET_CURRENT_TEMPERATURE)
                .queryParam("q", regionName)
                .toUriString();
    }
}
