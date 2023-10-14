package momongo12.fintech.services.remote;

import momongo12.fintech.api.dto.WeatherApiResponse;
import momongo12.fintech.services.exceptions.WeatherApiTokenKeyException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * @author Momongo12
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
public class WeatherApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherApiClient weatherApiClient;

    @Test
    void testGetCurrentWeatherWithValidRegionName() {
        String validRegionName = "kurgan";
        WeatherApiResponse mockResponse = new WeatherApiResponse();
        ResponseEntity<WeatherApiResponse> responseEntity = ResponseEntity.ok(mockResponse);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(WeatherApiResponse.class)))
                .thenReturn(responseEntity);

        Optional<WeatherApiResponse> weatherApiResponse = weatherApiClient.getCurrentWeather(validRegionName);

        assertTrue(weatherApiResponse.isPresent());
        assertEquals(mockResponse, weatherApiResponse.get());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(WeatherApiResponse.class));
    }

    @Test
    void testGetCurrentWeatherWithInvalidRegionName() {
        assertThrows(IllegalArgumentException.class, () -> weatherApiClient.getCurrentWeather(null));

        verify(restTemplate, never()).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(WeatherApiResponse.class));
    }

    @Test
    void testGetCurrentWeatherWithApiError() {
        String validRegionName = "kurgan";
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(WeatherApiResponse.class)))
                .thenThrow(new WeatherApiTokenKeyException("API token error"));

        Optional<WeatherApiResponse> weatherApiResponse = weatherApiClient.getCurrentWeather(validRegionName);

        assertFalse(weatherApiResponse.isPresent());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(WeatherApiResponse.class));
    }
}
