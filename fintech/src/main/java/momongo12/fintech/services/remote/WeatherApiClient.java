package momongo12.fintech.services.remote;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import momongo12.fintech.api.dto.WeatherApiResponse;
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
public class WeatherApiClient {

    final RestTemplate restTemplate;
    public static final String GET_CURRENT_TEMPERATURE = "/current.json";

    public WeatherApiClient(@Qualifier("restTemplateForWeatherApi") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<WeatherApiResponse> getCurrentWeather(String regionName) {
        ResponseEntity<WeatherApiResponse> response = restTemplate.
                exchange(
                        createUrlWithRegionName(regionName),
                        HttpMethod.GET,
                        null,
                        WeatherApiResponse.class);

        return Optional.ofNullable(response.getBody());
    }

    private String createUrlWithRegionName(String regionName) {
        return UriComponentsBuilder.newInstance()
                .path(GET_CURRENT_TEMPERATURE)
                .queryParam("q", regionName)
                .toUriString();
    }
}
