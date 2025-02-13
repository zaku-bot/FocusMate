package com.app.recommender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FocusMateRecommender {

    public static void main(String[] args) {
        FirebaseInitializer.initialize();
        SpringApplication.run(FocusMateRecommender.class, args);
    }
}