package com.app.recommender;

import com.app.recommender.model.UserPreferences;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

@Service
public class UserPreferenceService {

    private static final String USERS = "users";
    private Firestore firestore;

    // Getter for testing purposes
    Firestore getFirestore() {
        return firestore;
    }

    // Setter for injecting Firestore instance (for testing purposes)
    public void setFirestore(Firestore firestore) {
        this.firestore = firestore;
    }

    public UserPreferences getUserPreferences(String userId) {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference docRef = firestore.collection(USERS).document(userId);

        try {
            DocumentSnapshot document = docRef.get().get();
            if (document.exists()) {
                return document.toObject(UserPreferences.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}