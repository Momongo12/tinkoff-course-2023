package momongo12.fintech.services.remote;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import momongo12.fintech.api.dto.WeatherApiResponse;
import momongo12.fintech.api.mappers.WeatherMapper;
import momongo12.fintech.services.kafka.WeatherKafkaProducer;
import momongo12.fintech.store.entities.Weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author momongo12
 * @version 1.0
 */
@Component
@Log4j2
@RequiredArgsConstructor
public final class WeatherApiScheduler {

    @Value("${app.regionsForScheduler}")
    private String[] regionNames;
    private int currentRegionIndexForScheduler = 0;
    private final WeatherApiClient weatherApiClient;
    private final WeatherMapper weatherMapper;
    private final WeatherKafkaProducer kafkaProducer;

    @Scheduled(cron = "${app.cron.weatherApiScheduler}")
    private void getActualWeatherDataFromWeatherApi() {
        Optional<WeatherApiResponse> weatherApiResponse = weatherApiClient
                .getCurrentWeather(regionNames[currentRegionIndexForScheduler]);

        if (weatherApiResponse.isPresent()) {
            log.debug("Weather data from weather api received");
            Weather weather = weatherMapper.weatherApiResponseToWeather(weatherApiResponse.get());

            kafkaProducer.sendActualWeather(weather);
        } else {
            log.debug("Something went wrong while getting actual weather for region={}",
                    regionNames[currentRegionIndexForScheduler]);
        }

        currentRegionIndexForScheduler = (currentRegionIndexForScheduler + 1) % regionNames.length;
    }
}
