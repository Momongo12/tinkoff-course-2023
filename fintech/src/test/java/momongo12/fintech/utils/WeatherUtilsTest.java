package momongo12.fintech.utils;


import static org.junit.jupiter.api.Assertions.*;

import momongo12.fintech.store.entities.Weather;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WeatherUtilsTest {

    private static List<Weather> weatherListTestCase1;
    private static List<Weather> weatherListTestCase2;

    @BeforeAll
    public static void init() {
        weatherListTestCase1 = new ArrayList<>();
        weatherListTestCase1.add(WeatherFactory.createWeather("region1", 1.0));
        weatherListTestCase1.add(WeatherFactory.createWeather("region2", 2.0));
        weatherListTestCase1.add(WeatherFactory.createWeather("region3", 3.0));
        weatherListTestCase1.add(WeatherFactory.createWeather("region4", 4.0));
        weatherListTestCase1.add(WeatherFactory.createWeather("region5", 5.0));

        weatherListTestCase2 = new ArrayList<>();
        weatherListTestCase2.add(WeatherFactory.createWeather("region1", 1.0));
        weatherListTestCase2.add(WeatherFactory.createWeather("region2", 2.0));
        weatherListTestCase2.add(WeatherFactory.createWeather("region2", 3.0));
        weatherListTestCase2.add(WeatherFactory.createWeather("region4", 1.0));
        weatherListTestCase2.add(WeatherFactory.createWeather("region5", 5.0));
    }

    @Test
    public void testCalculateAverageTemperature() {
        double averageTemperature = WeatherUtils.calculateAverageTemperature(weatherListTestCase1);

        assertEquals(averageTemperature, 3.0, 0.1);
    }

    @Test
    public void testFindRegionsWithTemperatureLargerSome() {
        double targetTemperature = 3.0;

        List<String> regions = WeatherUtils.findRegionsWithTemperatureLargerSome(weatherListTestCase1, targetTemperature);

        assertNotNull(regions);
        assertTrue(regions.contains("region4"));
        assertTrue(regions.contains("region5"));
    }

    @Test
    public void testConvertToMapGroupingByRegionId() {
        int idSomeRegion = weatherListTestCase1.get(0).getRegionId();
        double temperatureSameRegion = weatherListTestCase1.get(0).getTemperatureValue();

        Map<Integer, List<Double>> convertedWeatherList = WeatherUtils.convertToMapGroupingByRegionId(weatherListTestCase1);

        assertNotNull(convertedWeatherList);
        assertEquals(5, convertedWeatherList.size());
        assertTrue(convertedWeatherList.containsKey(idSomeRegion));
        assertTrue(convertedWeatherList.get(idSomeRegion).contains(temperatureSameRegion));
    }

    @Test
    public void testConvertToMapGroupingByRegionIdWithRepeatingRegionId() {
        int idSomeRegion = weatherListTestCase2.get(0).getRegionId();
        double temperatureSameRegion = weatherListTestCase2.get(0).getTemperatureValue();

        Map<Integer, List<Double>> convertedWeatherList = WeatherUtils.convertToMapGroupingByRegionId(weatherListTestCase2);

        assertNotNull(convertedWeatherList);
        assertEquals(4, convertedWeatherList.size());
        assertTrue(convertedWeatherList.containsKey(idSomeRegion));
        assertTrue(convertedWeatherList.get(idSomeRegion).contains(temperatureSameRegion));
    }

    @Test
    public void testConvertToMapGroupingByTemperature() {
        Double temperatureSomeRegion = weatherListTestCase1.get(0).getTemperatureValue();
        Weather weatherObjectWithSameTemperature = weatherListTestCase1.get(0);

        Map<Double, Collection<Weather>> convertedWeatherList = WeatherUtils.convertToMapGroupingByTemperature(weatherListTestCase1);

        assertNotNull(convertedWeatherList);
        assertEquals(5, convertedWeatherList.size());
        assertTrue(convertedWeatherList.containsKey(temperatureSomeRegion));
        assertTrue(convertedWeatherList.get(temperatureSomeRegion).contains(weatherObjectWithSameTemperature));
    }

    @Test
    public void testConvertToMapGroupingByTemperatureWithRepeatingTemperature() {
        Double temperatureSomeRegion = weatherListTestCase2.get(0).getTemperatureValue();
        Weather weatherObjectWithSameTemperature = weatherListTestCase2.get(0);

        Map<Double, Collection<Weather>> convertedWeatherList = WeatherUtils.convertToMapGroupingByTemperature(weatherListTestCase2);

        assertNotNull(convertedWeatherList);
        assertEquals(4, convertedWeatherList.size());
        assertTrue(convertedWeatherList.containsKey(temperatureSomeRegion));
        assertTrue(convertedWeatherList.get(temperatureSomeRegion).contains(weatherObjectWithSameTemperature));
    }
}
