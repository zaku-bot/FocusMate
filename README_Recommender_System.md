# CSE-535 FocusMate - Recommender System 

### Author - Zakiya Ali

## Description
This is a Spring Boot application designed to suggest well-timed breaks and personalized leisure activities taking into account user preferences and contextual parameters such as weather and traffic information to tailor recommendations.

## Prerequisites
Make sure you have the following installed on your machine:
- Java (JDK 8 or later)
- MavenSetup system environment on your local 
- download the source code in the zip file
- servicekey.json file is also present in the zip. Note its path

## Getting Started
Follow these steps to run the Recommender System application locally:

### 1. Navigate to the project directory
```bash
   cd /path/to/the/downloaded/project/directory 
```
### 2. Build the Project
```bash
mvn clean install
```

### 3. Run the Application
```bash
export SERVICE_ACCOUNT_PATH="path/to/your/servicekey.json"
java -jar target/recommender-1.0.0.jar
```

Example
```bash
export SERVICE_ACCOUNT_PATH="/Users/zakiya/Documents/servicekey.json"
java -jar target/recommender-1.0.0.jar
```
The application will be up at localhost:8080

### 4. Make API call

API call format
```thymeleafurlexpressions
http://localhost:8080/api/recommend?userId=<userID>&latitude=<user-latitude>&longitude=<user-longitude>&localTime=<user-local time>&dateFilter=<date-filter>&distanceFilter=<distance filter>
```

API params:
- userId (String): userId gets generated when the user first signs up on the FocusMate app and gets stored in cloud firestore
- latitude (String): users location data sent from the application
- longitude (String): users location data sent from the application
- localTime (String): users local time sent from the application
- dateFilter (String): TODAY, THIS_WEEK, THIS_MONTH selected on the UI dropdown
- distanceFilter (String): - LESS_THAN_10_MILES, BETWEEN_10_AND_20_MILES, GREATER_THAN_20_MILES 

Example GET Request
```thymeleafurlexpressions
http://localhost:8080/api/recommend?userId=H6KY1R3hJkhfHRiclKnZ4gb7yRs1&latitude=33.481840&longitude=-112.033610&localTime=12:00%20PM%20MST&dateFilter=THIS_WEEK&distanceFilter=GREATER_THAN_20_MILES```
```

Example JSON Response
```json
{
    "recommendations": [{
        "event": {
            "eventName": "The Attic",
            "eventType": "Night Club",
            "eventDate": "2023-11-30",
            "eventTime": "9:00 PM",
            "eventLocationName": "4247 E Indian School Rd #102, Phoenix, AZ 85018",
            "eventLocation": {
                "latitude": "33.494629",
                "longitude": "-111.989342"
            },
            "additionalDetails": {
                "bookingLink": "https://www.theatticbarandlounge.com/",
                "cost": "$20-$30"
            }
        },
        "weatherData": {
            "weather": "overcast clouds",
            "temperature": 288.64,
            "humidity": 53
        },
        "trafficData": {
            "distance": 5584.0,
            "duration": 577,
            "durationInTraffic": 569,
            "trafficDetails": "Low Traffic"
        }
    }, {
        "event": {
            "eventName": "The Mission Old Town",
            "eventType": "Restaurant",
            "eventDate": "2023-12-01",
            "eventTime": "6:00 PM",
            "eventLocationName": "3815 N Brown Ave, Scottsdale, AZ 85251",
            "eventLocation": {
                "latitude": "33.491791",
                "longitude": "-111.924217"
            },
            "additionalDetails": {
                "bookingLink": "https://www.opentable.com/r/the-mission-tempe-az?restref=3405294&utm_source=google&utm_medium=organic&utm_campaign=google_restaurant_listing",
                "cost": "$30-$50"
            }
        },
        "weatherData": {
            "weather": "overcast clouds",
            "temperature": 288.64,
            "humidity": 53
        },
        "trafficData": {
            "distance": 12339.0,
            "duration": 1200,
            "durationInTraffic": 1251,
            "trafficDetails": "Low Traffic"
        }
    }]
}
```