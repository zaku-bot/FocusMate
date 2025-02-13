package com.app.recommender.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDetails {
    private String bookingLink;
    private String cost;
}
