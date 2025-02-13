package com.app.recommender;

import com.app.recommender.model.Event;
import com.app.recommender.model.EventDetails;
import com.app.recommender.model.Location;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

public class FirestoreDataInsertion {

    public static void main(String[] args) {
        FirebaseInitializer.initialize();
        Firestore db = FirestoreClient.getFirestore();

        Event event = new Event();
        event.setEventName("The Mission Old Town");
        event.setEventType("Restaurant");
        event.setEventDate("2023-12-01");
        event.setEventTime("6:00 PM");
        event.setEventLocationName("3815 N Brown Ave, Scottsdale, AZ 85251");
        event.setEventLocation(new Location("33.491791", "-111.924217")); // Replace with actual coordinates
        event.setAdditionalDetails(new EventDetails("https://www.opentable.com/r/the-mission-tempe-az?restref=3405294&utm_source=google&utm_medium=organic&utm_campaign=google_restaurant_listing", "$30-$50"));

        Event event2 = new Event();
        event2.setEventName("The Mission Old Town");
        event2.setEventType("Restaurant");
        event2.setEventDate("2023-12-01");
        event2.setEventTime("6:00 PM");
        event2.setEventLocationName("3815 N Brown Ave, Scottsdale, AZ 85251");
        event2.setEventLocation(new Location("33.491791", "-111.924217")); // Replace with actual coordinates
        event2.setAdditionalDetails(new EventDetails("https://www.ticketmaster.com/the-killers-tickets/artist/2087518", "$30-$50"));

        Event event3 = new Event();
        event3.setEventName("The Killers");
        event3.setEventType("Concert");
        event3.setEventDate("2023-12-12");
        event3.setEventTime("8:00 PM");
        event3.setEventLocationName("Tempe Beach Park Amphitheatre");
        event3.setEventLocation(new Location("33.494629", "-111.989342")); // Replace with actual coordinates
        event3.setAdditionalDetails(new EventDetails("https://www.theatticbarandlounge.com/", "$50-$150"));

        addEventToFirestore(db, event);
        addEventToFirestore(db, event2);
        addEventToFirestore(db, event3);

    }

    private static void addEventToFirestore(Firestore db, Event event) {
        // Replace with your Firestore collection name
        String collectionName = "testevents";

        // Add data to the collection
        DocumentReference docRef = db.collection(collectionName).document();
        ApiFuture<WriteResult> result = docRef.set(event);

        // Handle result
        try {
            System.out.println("Update time : " + result.get().getUpdateTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

