package com.app.recommender;

import com.app.recommender.model.Location;
import com.app.recommender.model.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
public class WeatherService {

    @Value("${weather.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherData getWeatherData(Location location) throws Exception {
        String apiUrl = UriComponentsBuilder.fromUriString("https://api.openweathermap.org/data/3.0/onecall?")
                .queryParam("lat", location.getLatitude())
                .queryParam("lon", location.getLongitude())
                .queryParam("exclude", "minutely,hourly,daily")
                .queryParam("appid", apiKey)
                .toUriString();

        return parseWeatherData(restTemplate.getForObject(apiUrl, String.class));
    }

    WeatherData parseWeatherData(String jsonData) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonData);

        double temperature = rootNode.path("current").path("temp").asDouble();
        int humidity = rootNode.path("current").path("humidity").asInt();
        String weatherDescription = rootNode.path("current").path("weather").get(0).path("description").asText();

        System.out.println("Temperature: " + temperature);
        System.out.println("Humidity: " + humidity);
        System.out.println("Weather Description: " + weatherDescription);
        return new WeatherData(weatherDescription, temperature, humidity);
    }
}
