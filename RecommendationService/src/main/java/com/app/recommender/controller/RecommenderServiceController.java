package com.app.recommender.controller;

import com.app.recommender.EventRecommendationService;
import com.app.recommender.model.Recommendation;
import com.app.recommender.model.RecommendationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecommenderServiceController {
    private final EventRecommendationService eventRecommendationService;

    @Autowired
    public RecommenderServiceController(EventRecommendationService eventRecommendationService) {
        this.eventRecommendationService = eventRecommendationService;
    }

    @GetMapping
    public String getAllPersons() {
        return "Hello";
    }


    @GetMapping("/recommend")
    public RecommendationResponse getRecommendations(
            @RequestParam String userId,
            @RequestParam String latitude,
            @RequestParam String longitude,
            @RequestParam String localTime,
            @RequestParam String dateFilter,
            @RequestParam String distanceFilter
    ) {
        List<Recommendation> recommendations = eventRecommendationService.getRecommendations(userId, latitude, longitude, localTime, dateFilter, distanceFilter);
        return new RecommendationResponse(recommendations);
    }
}
