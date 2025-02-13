package com.app.recommender.model;

import lombok.Data;
import lombok.Getter;

import java.util.Optional;

@Data
public class TrafficData {

    public static final double LOW_THRESHOLD = 2.0;
    public static final double HIGH_THRESHOLD = 5.0;
    private double distance;
    private long duration;
    private long durationInTraffic;
    private String trafficDetails;

    public TrafficData(double distance, long duration, long durationInTraffic) {
        this.distance = distance;
        this.duration = duration;
        this.durationInTraffic = durationInTraffic;
        this.trafficDetails = determineTrafficCondition();
    }

    public String determineTrafficCondition() {
        double avgSpeed = distance / duration;
        double trafficSpeed = distance / durationInTraffic;
        if (isLowTraffic(avgSpeed, trafficSpeed)) {
            return TrafficCondition.LOW_TRAFFIC.getLabel();
        } else if (isHighTraffic(avgSpeed, trafficSpeed)) {
            return TrafficCondition.HIGH_TRAFFIC.getLabel();
        } else {
            return TrafficCondition.MODERATE_TRAFFIC.getLabel();
        }

    }

    private boolean isLowTraffic(double avgSpeed, double trafficSpeed) {
        return trafficSpeed <= avgSpeed || Math.abs(trafficSpeed - avgSpeed) <= LOW_THRESHOLD;
    }

    private boolean isHighTraffic(double avgSpeed, double trafficSpeed) {
        return trafficSpeed - avgSpeed > HIGH_THRESHOLD;
    }


    @Getter
    private enum TrafficCondition {
        LOW_TRAFFIC("Low Traffic"),
        MODERATE_TRAFFIC("Moderate Traffic"),
        HIGH_TRAFFIC("High Traffic");

        private final String label;
        TrafficCondition(String label) {
            this.label = label;
        }

    }
}
