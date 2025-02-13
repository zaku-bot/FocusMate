package com.app.recommender;

import com.app.recommender.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class TrafficInfoService {

    @Value("${google-maps.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public TrafficInfoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TrafficData getTrafficInfo(Location origin, Location destination) throws IOException {
        if(origin == null || destination == null)
                return null;
        String apiUrl = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/distancematrix/json")
                .queryParam("origins", getTrafficInputString(origin))
                .queryParam("destinations", getTrafficInputString(destination))
                .queryParam("departure_time", "now")
                .queryParam("key", apiKey)
                .toUriString();
        TrafficApiResponse trafficApiResponse = restTemplate.getForObject(apiUrl, TrafficApiResponse.class);

        TrafficData trafficData = parseTrafficInfo(trafficApiResponse);
        System.out.println("Traffic Information: " + trafficData);
        return trafficData;

    }

    private static TrafficData parseTrafficInfo(TrafficApiResponse trafficApiResponse) {
        if ("OK".equals(trafficApiResponse.getStatus())) {
            List<TrafficApiResponseRow> rows = trafficApiResponse.getRows();
            if (rows != null && !rows.isEmpty()) {
                TrafficApiResponseRow trafficAPIResponseRow = rows.get(0);
                List<TrafficApiResponseElement> trafficApiResponseElements = trafficAPIResponseRow.getElements();
                if (trafficApiResponseElements != null && !trafficApiResponseElements.isEmpty()) {
                    TrafficApiResponseElement trafficApiResponseElement = trafficApiResponseElements.get(0);
                    long distance = trafficApiResponseElement.getDistance().getValue();
                    long duration = trafficApiResponseElement.getDuration().getValue();
                    long durationInTraffic = trafficApiResponseElement.getDurationInTraffic().getValue();
                    return new TrafficData(distance, duration, durationInTraffic);
                }
            }
        }

        return null;
    }

    private String getTrafficInputString(Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }
}

