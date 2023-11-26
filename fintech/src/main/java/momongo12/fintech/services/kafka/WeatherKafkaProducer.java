package momongo12.fintech.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import momongo12.fintech.store.entities.Weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author momongo12
 * @version 1.0
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class WeatherKafkaProducer {

    @Value("${app.kafka.topicNameForActualWeatherData}")
    private String TOPIC_NAME;

    private final KafkaTemplate<String, Weather> kafkaTemplate;

    /**
     * Sends actual weather data to the configured Kafka topic.
     *
     * @param weather The Weather object representing the actual weather data to be sent.
     */
    public void sendActualWeather(Weather weather) {
        kafkaTemplate.send(TOPIC_NAME, weather.getRegion().getName(), weather);
        log.debug("Kafka producer produced the weather data {}", weather);
    }
}
