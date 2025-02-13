package com.app.recommender;

import com.app.recommender.model.Location;
import com.app.recommender.model.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        weatherService = new WeatherService(restTemplate);
    }


    @Test
    void testGetWeatherData() throws Exception {
        // Given
        Location location = new Location("37.7749", "-122.4194");
        String jsonResponse = "{ \"current\": { \"temp\": 25.5, \"humidity\": 80, \"weather\": [ { \"description\": \"Clear\" } ] } }";

        when(restTemplate.getForObject(anyString(), any())).thenReturn(jsonResponse);

        WeatherData weatherData = weatherService.getWeatherData(location);

        assertEquals("Clear", weatherData.getWeather());
        assertEquals(25.5, weatherData.getTemperature());
        assertEquals(80, weatherData.getHumidity());
    }

    @Test
    void testParseWeatherData() throws Exception {
        String jsonResponse = "{ \"current\": { \"temp\": 25.5, \"humidity\": 80, \"weather\": [ { \"description\": \"Clear\" } ] } }";

        WeatherData weatherData = weatherService.parseWeatherData(jsonResponse);

        assertEquals("Clear", weatherData.getWeather());
        assertEquals(25.5, weatherData.getTemperature());
        assertEquals(80, weatherData.getHumidity());
    }
}

