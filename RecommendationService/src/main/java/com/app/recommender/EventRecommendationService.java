package com.app.recommender;

import com.app.recommender.model.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class EventRecommendationService {

    private static final String EVENTS_COLLECTION = "testevents";
    private static final String EVENT_TYPE = "eventType";
    private static final String EVENT_DATE = "eventDate";

    private final WeatherService weatherService;
    private final TrafficInfoService trafficInfoService;

    private final UserPreferenceService userPreferenceService;

    public EventRecommendationService(WeatherService weatherService, TrafficInfoService trafficInfoService, UserPreferenceService userPreferenceService) {
        this.weatherService = weatherService;
        this.trafficInfoService = trafficInfoService;
        this.userPreferenceService = userPreferenceService;
    }

    private List<Event> getRecommendedEvents(UserPreferences userPreferences, Location userLocation, String dateFilter, String distanceFilter) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Query query = db.collection(EVENTS_COLLECTION)
                .whereIn(EVENT_TYPE, userPreferences.getPreferences());

        LocalDate now = LocalDate.now();

        switch (EventDateFilter.valueOf(dateFilter)) {
            case TODAY:
                query = query.whereGreaterThanOrEqualTo(EVENT_DATE, now.format(DateTimeFormatter.ISO_DATE))
                        .whereLessThan(EVENT_DATE, now.plusDays(1).format(DateTimeFormatter.ISO_DATE));
                break;
            case THIS_WEEK:
                LocalDate endOfWeek = now.plusWeeks(1);
                query = query.whereGreaterThanOrEqualTo(EVENT_DATE, now.format(DateTimeFormatter.ISO_DATE))
                        .whereLessThan(EVENT_DATE, endOfWeek.format(DateTimeFormatter.ISO_DATE));
                break;
            case THIS_MONTH:
                LocalDate endOfMonth = now.plusMonths(1);
                query = query.whereGreaterThanOrEqualTo(EVENT_DATE, now.format(DateTimeFormatter.ISO_DATE))
                        .whereLessThan(EVENT_DATE, endOfMonth.format(DateTimeFormatter.ISO_DATE));
                break;
        }

        query = query.orderBy(EVENT_DATE);

       List<Event> recommendedEvents = new ArrayList<>();

        try {
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                Event event = document.toObject(Event.class);
                recommendedEvents.add(event);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        recommendedEvents = applyDistanceFilter(recommendedEvents, distanceFilter, userLocation);
        return recommendedEvents;
    }

    public List<Recommendation> getRecommendations(String userId, String latitude, String longitude, String localTime, String dateFilter, String distanceFilter) {
        List<Recommendation> recommendations = new ArrayList<>();
        try {
            Location userLocation = new Location(latitude, longitude);
            UserPreferences userPreferences = userPreferenceService.getUserPreferences(userId);
            List<Event> events = getRecommendedEvents(userPreferences, userLocation, dateFilter, distanceFilter);

            for (Event event : events) {
                WeatherData weatherData = weatherService.getWeatherData(userLocation);
                TrafficData trafficData = trafficInfoService.getTrafficInfo(userLocation, event.getEventLocation());
                recommendations.add(new Recommendation(event, weatherData, trafficData));
            }
        } catch (Exception ignored) {
        }
        return recommendations;
    }

    private List<Event> applyDistanceFilter(List<Event> recommendedEvents, String distanceFilter, Location userLocation) {
        if (userLocation != null &!recommendedEvents.isEmpty()) {
            double maxDistance = getMaxDistance(distanceFilter);

            double userLatitude = Double.parseDouble(userLocation.getLatitude());
            double userLongitude = Double.parseDouble(userLocation.getLongitude());

            double multiplier = 69.0; // 1 degree latitude/longitude is approximately 69 miles

            double maxLatitude = userLatitude + (maxDistance / multiplier);
            double minLatitude = userLatitude - (maxDistance / multiplier);

            double maxLongitude = Math.abs(userLongitude) + (maxDistance / multiplier);
            double minLongitude = Math.abs(userLongitude) - (maxDistance / multiplier);

            recommendedEvents = recommendedEvents.stream().filter(event -> Double.parseDouble(event.getEventLocation().getLatitude()) >= minLatitude &&
                    Double.parseDouble(event.getEventLocation().getLatitude()) <= maxLatitude).collect(Collectors.toList());
        }

        return recommendedEvents;
    }

    private double getMaxDistance(String distanceFilter) {
        switch (EventDistanceFilter.valueOf(distanceFilter)) {
            case LESS_THAN_10_MILES:
                return 10.0;
            case BETWEEN_10_AND_20_MILES:
                return 20.0;
            case GREATER_THAN_20_MILES:
                return Double.MAX_VALUE;
            default:
                return Double.MAX_VALUE;
        }
    }
}
