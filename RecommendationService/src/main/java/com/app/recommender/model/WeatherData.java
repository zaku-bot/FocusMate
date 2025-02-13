package com.app.recommender.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherData {
    private String weather;
    private double temperature;
    private int humidity;
}

