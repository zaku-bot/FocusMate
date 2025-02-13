package com.app.recommender.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TrafficApiResponseRow {

    @JsonProperty("elements")
    private List<TrafficApiResponseElement> trafficApiResponseElements;

    public List<TrafficApiResponseElement> getElements() {
        return trafficApiResponseElements;
    }

    public void setElements(List<TrafficApiResponseElement> trafficApiResponseElements) {
        this.trafficApiResponseElements = trafficApiResponseElements;
    }
}
