package com.app.recommender.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrafficApiResponseElement {

    @JsonProperty("distance")
    private TrafficApiResponseDistance trafficApiResponseDistance;

    @JsonProperty("duration")
    private TrafficApiResponseDuration trafficApiResponseDuration;

    @JsonProperty("duration_in_traffic")
    private TrafficApiResponseDuration trafficApiResponseDurationInTraffic;

    @JsonProperty("status")
    private String status;

    public TrafficApiResponseDistance getDistance() {
        return trafficApiResponseDistance;
    }

    public void setDistance(TrafficApiResponseDistance trafficApiResponseDistance) {
        this.trafficApiResponseDistance = trafficApiResponseDistance;
    }

    public TrafficApiResponseDuration getDuration() {
        return trafficApiResponseDuration;
    }

    public void setDuration(TrafficApiResponseDuration trafficApiResponseDuration) {
        this.trafficApiResponseDuration = trafficApiResponseDuration;
    }

    public TrafficApiResponseDuration getDurationInTraffic() {
        return trafficApiResponseDurationInTraffic;
    }

    public void setDurationInTraffic(TrafficApiResponseDuration trafficApiResponseDurationInTraffic) {
        this.trafficApiResponseDurationInTraffic = trafficApiResponseDurationInTraffic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
