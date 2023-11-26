package momongo12.fintech.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import momongo12.fintech.store.entities.Region;
import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.RegionRepository;
import momongo12.fintech.store.repositories.WeatherRepository;
import momongo12.fintech.store.repositories.WeatherTypeRepository;
import momongo12.fintech.utils.WeatherFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author momongo12
 * @version 1.0
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class WeatherKafkaConsumer {

    @Value("${app.periodsNumberForMovingAverage:30}")
    private long numberPeriods;
    private WeatherRepository weatherRepository;
    private final RegionRepository regionRepository;
    private final WeatherTypeRepository weatherTypeRepository;
    private final WeatherFactory weatherFactory;

    @KafkaListener(topics = "${app.kafka.topicNameForActualWeatherData}")
    public void handleNewWeatherData(Weather weather) {
        log.debug("Consumer consume kafka message -> {}", weather);
        String regionName = weather.getRegion().getName();

        saveNewWeatherDataToDb(weather);

        long numberPeriodsForMovingAverage = getNumberPeriodsForMovingAverage(regionName);

        Double movingAverage = weatherRepository.calculateMovingAverage(
                weatherFactory.getRegionIdByRegionName(regionName),
                numberPeriodsForMovingAverage
        );

        log.info("Moving average for the {} region for {} periods = {}",
                regionName, numberPeriodsForMovingAverage, movingAverage);
    }

    private void saveNewWeatherDataToDb(Weather weather) {
        Region region = weather.getRegion();

        weather.getRegion().setId(weatherFactory.getRegionIdByRegionName(region.getName()));
        weatherTypeRepository.saveAndFlush(weather.getWeatherType());
        regionRepository.saveAndFlush(weather.getRegion());
        weatherRepository.addWeatherData(weather);
    }

    private long getNumberPeriodsForMovingAverage(String regionName) {
        long numberWeatherData = weatherRepository.countByRegionId(
                weatherFactory.getRegionIdByRegionName(regionName)
        );

        return (numberWeatherData < numberPeriods) ? numberWeatherData % numberPeriods : numberPeriods;
    }

    @Autowired
    public void setWeatherRepository(@Qualifier("weatherRepositoryForWeatherServiceImpl")
                                     WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }
}
