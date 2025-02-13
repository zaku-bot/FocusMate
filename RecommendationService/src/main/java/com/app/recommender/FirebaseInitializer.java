package com.app.recommender;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;

public class FirebaseInitializer {
    public static void initialize() {
        try {
            String serviceAccountPath = System.getenv("SERVICE_ACCOUNT_PATH");
            FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://fir-auth-7e9d6.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

