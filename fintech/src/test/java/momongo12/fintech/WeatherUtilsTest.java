package momongo12.fintech;


import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WeatherUtilsTest {

    private List<Weather> weatherList;

    @BeforeEach
    public void init() {
        weatherList = new ArrayList<>();
        weatherList.add(WeatherFactory.createWeather("region1", 1.0));
        weatherList.add(WeatherFactory.createWeather("region2", 2.0));
        weatherList.add(WeatherFactory.createWeather("region3", 3.0));
        weatherList.add(WeatherFactory.createWeather("region4", 4.0));
        weatherList.add(WeatherFactory.createWeather("region5", 5.0));
    }

    @Test
    public void testCalculateAverageTemperature() {
        double averageTemperature = WeatherUtils.calculateAverageTemperature(weatherList);

        assertEquals(averageTemperature, 3.0, 0.1);
    }

    @Test
    public void testFindRegionsWithTemperatureLargerSome() {
        double targetTemperature = 3.0;

        List<String> regions = WeatherUtils.findRegionsWithTemperatureLargerSome(weatherList, targetTemperature);

        assertNotNull(regions);
        assertTrue(regions.contains("region4"));
        assertTrue(regions.contains("region5"));
    }

    @Test
    public void testConvertToMapGroupingByRegionId() {
        int idSomeRegion = weatherList.get(0).getRegionId();
        double temperatureSameRegion = weatherList.get(0).getTemperatureValue();

        Map<Integer, List<Double>> convertedWeatherList = WeatherUtils.convertToMapGroupingByRegionId(weatherList);

        assertNotNull(convertedWeatherList);
        assertEquals(5, convertedWeatherList.size());
        assertTrue(convertedWeatherList.containsKey(idSomeRegion));
        assertTrue(convertedWeatherList.get(idSomeRegion).contains(temperatureSameRegion));
    }

    @Test
    public void testConvertToMapGroupingByTemperature() {
        Integer temperatureSomeRegion = (int) weatherList.get(0).getTemperatureValue();
        Weather weatherObjectWithSameTemperature = weatherList.get(0);

        Map<Integer, Collection<Weather>> convertedWeatherList = WeatherUtils.convertToMapGroupingByTemperature(weatherList);

        assertNotNull(convertedWeatherList);
        assertEquals(5, convertedWeatherList.size());
        assertTrue(convertedWeatherList.containsKey(temperatureSomeRegion));
        assertTrue(convertedWeatherList.get(temperatureSomeRegion).contains(weatherObjectWithSameTemperature));
    }
}
