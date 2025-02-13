package com.app.recommender.model;

import com.google.cloud.firestore.DocumentSnapshot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private String eventName;
    private String eventType;
    private String eventDate;
    private String eventTime;
    private String eventLocationName;
    private Location eventLocation;
    private EventDetails additionalDetails;

}
