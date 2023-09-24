package momongo12.fintech;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Momongo12
 * @version 1.0
 */
public class WeatherUtils {

    private WeatherUtils() {}

    /**
     * Calculates the average temperature from a list of Weather objects.
     *
     * @param weatherList List of Weather objects.
     * @return The average temperature, or 0.0 if the list is empty.
     */
    public static double calculateAverageTemperature(List<Weather> weatherList) {
        return weatherList
                .stream()
                .mapToDouble(Weather::getTemperatureValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Finds regions with temperatures higher than the specified value.
     *
     * @param weatherList List of Weather objects.
     * @param temperature The threshold temperature.
     * @return A list of region names with temperatures higher than the specified value.
     */
    public static List<String> findRegionsWithTemperatureLargerSome(List<Weather> weatherList, double temperature) {
        return weatherList
                .stream()
                .filter(weather -> Double.compare(temperature, weather.getTemperatureValue()) < 0)
                .map(Weather::getRegionName)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of Weather objects into a map grouped by regionId.
     *
     * @param weatherList List of Weather objects.
     * @return A map where keys are regionId and values are lists of temperature values.
     */
    public static Map<Integer, List<Double>> convertToMapGroupingByRegionId(List<Weather> weatherList) {
        return weatherList.stream()
                .collect(Collectors.groupingBy(
                        Weather::getRegionId,
                        Collectors.mapping(Weather::getTemperatureValue, Collectors.toList())
                ));
    }

    /**
     * Converts a list of Weather objects into a map grouped by temperature
     *
     * @param weatherList List of Weather objects.
     * @return A map where keys are rounded temperatures and values are collections of Weather objects.
     * @implNote In the process, the type of the temperature value is converted to Integer
     */
    public static Map<Integer, Collection<Weather>> convertToMapGroupingByTemperature(List<Weather> weatherList) {
        return weatherList.stream()
                .collect(Collectors.groupingBy(
                        weather -> (int) weather.getTemperatureValue(),
                        Collectors.toCollection(ArrayList::new)
                ));
    }
}
