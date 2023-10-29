package momongo12.fintech.services.remote;

import momongo12.fintech.api.dto.WeatherApiResponse;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * @author Momongo12
 * @version 2.0
 */
public class WeatherApiClientTest {

    private WeatherApiClient weatherApiClient;
    private StubRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new StubRestTemplate();
        weatherApiClient = new WeatherApiClient(restTemplate);
    }

    @Test
    void testGetCurrentWeatherWithValidRegionName() {
        String validRegionName = "kurgan";
        WeatherApiResponse stubResponse = new WeatherApiResponse();
        restTemplate.setStubResponse(stubResponse);

        Optional<WeatherApiResponse> weatherApiResponse = weatherApiClient.getCurrentWeather(validRegionName);

        assertTrue(weatherApiResponse.isPresent());
        assertEquals(stubResponse, weatherApiResponse.get());
    }

    @Test
    void testGetCurrentWeatherWithInvalidRegionName() {
        assertThrows(IllegalArgumentException.class, () -> weatherApiClient.getCurrentWeather(null));
    }

    @Test
    void testGetCurrentWeatherWithApiError() {
        String validRegionName = "kurgan";
        restTemplate.setStubResponse(null);

        Optional<WeatherApiResponse> weatherApiResponse = weatherApiClient.getCurrentWeather(validRegionName);

        assertFalse(weatherApiResponse.isPresent());
    }

    private static class StubRestTemplate extends RestTemplate {

        private WeatherApiResponse stubResponse;

        public void setStubResponse(WeatherApiResponse stubResponse) {
            this.stubResponse = stubResponse;
        }

        @Override
        public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables)
                throws RestClientException {
            if (responseType.isAssignableFrom(WeatherApiResponse.class)) {
                return ResponseEntity.ok((T) stubResponse);
            }
            return super.exchange(url, method, requestEntity, responseType, uriVariables);
        }
    }
}