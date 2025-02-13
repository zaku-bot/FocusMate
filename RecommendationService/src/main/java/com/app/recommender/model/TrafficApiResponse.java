package com.app.recommender.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TrafficApiResponse {

    @JsonProperty("destination_addresses")
    private List<String> destinationAddresses;

    @JsonProperty("origin_addresses")
    private List<String> originAddresses;

    @JsonProperty("rows")
    private List<TrafficApiResponseRow> trafficApiResponseRows;

    @JsonProperty("status")
    private String status;

    // Getter and Setter methods

    public List<String> getDestinationAddresses() {
        return destinationAddresses;
    }

    public void setDestinationAddresses(List<String> destinationAddresses) {
        this.destinationAddresses = destinationAddresses;
    }

    public List<String> getOriginAddresses() {
        return originAddresses;
    }

    public void setOriginAddresses(List<String> originAddresses) {
        this.originAddresses = originAddresses;
    }

    public List<TrafficApiResponseRow> getRows() {
        return trafficApiResponseRows;
    }

    public void setRows(List<TrafficApiResponseRow> trafficApiResponseRows) {
        this.trafficApiResponseRows = trafficApiResponseRows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

