package momongo12.fintech.store.repositories.impl;

import lombok.RequiredArgsConstructor;

import momongo12.fintech.store.entities.Region;
import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.entities.WeatherType;
import momongo12.fintech.store.repositories.WeatherRepository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author Momongo12
 * @version 1.1
 */
@Repository(value = "WeatherJdbcRepository")
@RequiredArgsConstructor
public class WeatherJdbcRepository implements WeatherRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Weather> findTemperatureDataByRegionId(int regionId) {
        String sql = """
                     SELECT w.*, wt.*, r.*
                     FROM weather w
                     LEFT JOIN weather_type wt ON w.weather_type_id = wt.id
                     JOIN region r ON w.region_id = r.id
                     WHERE w.region_id = ?
                     """;

        return jdbcTemplate.queryForStream(sql, new WeatherRowMapper(), regionId).toList();
    }

    @Override
    public Optional<Weather> findWeatherByRegionIdAndMeasuringDate(int regionId, Instant measuringDate) {
        String sql = """
                     SELECT w.*, wt.*, r.*
                     FROM weather w
                     LEFT JOIN weather_type wt ON w.weather_type_id = wt.id
                     JOIN region r ON w.region_id = r.id
                     WHERE w.region_id = ? AND w.measuring_date = ?
                     """;
        Object[] params = {regionId, Date.from(measuringDate)};

        try {
            Weather weather = jdbcTemplate.queryForObject(sql, params, new WeatherRowMapper());

            return Optional.ofNullable(weather);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Weather addWeatherData(Weather weather) {
        String sql = "INSERT INTO weather (temperature, measuring_date, weather_type_id, region_id) VALUES (?, ?, ?, ?)";
        Object[] params = {
                weather.getTemperatureValue(),
                Date.from(weather.getMeasuringDate()),
                (weather.getWeatherType() == null) ? null : weather.getWeatherType().getId(),
                weather.getRegion().getId()
        };

        jdbcTemplate.update(sql, params);

        return weather;
    }

    @Override
    public void updateTemperatureById(int weatherId, double newTemperature) {
        String sql = "UPDATE weather SET temperature = ? WHERE id = ?";

        jdbcTemplate.update(sql, ps -> {
            ps.setDouble(1, newTemperature);
            ps.setInt(2, weatherId);
        });
    }

    @Override
    public int deleteWeatherDataByRegionId(int regionId) throws NoSuchElementException {
        String sql = "DELETE FROM weather WHERE region_id = ?";

        int rowsAffected = jdbcTemplate.update(sql, regionId);

        if (rowsAffected == 0) {
            throw new NoSuchElementException("Weather data for region with regionId=%d not found".formatted(regionId));
        }
        return rowsAffected;
    }

    private static class WeatherRowMapper implements RowMapper<Weather> {
        @Override
        public Weather mapRow(ResultSet rs, int rowNum) throws SQLException {
            Weather weather = new Weather();
            weather.setId(rs.getInt("id"));
            weather.setTemperatureValue(rs.getDouble("temperature"));
            weather.setMeasuringDate(rs.getTimestamp("measuring_date").toInstant());

            WeatherType weatherType = new WeatherType();
            weatherType.setId(rs.getInt("weather_type_id"));
            weatherType.setDescription(rs.getString("description"));
            weatherType.setRepresentation(rs.getString("representation"));
            weather.setWeatherType(weatherType);

            Region region = new Region();
            region.setId(rs.getInt("region_id"));
            region.setName(rs.getString("name"));
            weather.setRegion(region);

            return weather;
        }
    }
}
