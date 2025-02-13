package com.app.recommender.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Recommendation {
    private Event event;
    private WeatherData weatherData;
    private TrafficData trafficData;
}
