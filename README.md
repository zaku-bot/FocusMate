# FocusMate - Comprehensive System

## Description
FocusMate is an integrated system designed to enhance productivity by providing personalized break recommendations, managing notifications, facilitating user authentication, and ensuring seamless data management. The system consists of four core components:

1. **Recommender System:** Suggests well-timed breaks and leisure activities based on user preferences, weather, and traffic conditions.
2. **Notification System:** Sends personalized push notifications to users based on their preferences and locations.
3. **User Management System:** Handles authentication, authorization, and user preferences.
4. **Task & Event Organizer:** Blocks distracting notifications and organizes tasks and events effectively.

---

## Prerequisites
Ensure you have the following installed:

- Java (JDK 8 or later)
- Maven
- .NET Framework v3.5 and v4.8
- Node.js & npm
- Firebase/Firestore account
- Google Cloud API Key

---

## Installation & Setup

### 1. Clone the Repository
```bash
   git clone https://github.com/your-username/focusmate.git
   cd focusmate
```

### 2. Set Up Recommender System
```bash
   cd recommender-system
   mvn clean install
   export SERVICE_ACCOUNT_PATH="path/to/your/servicekey.json"
   java -jar target/recommender-1.0.0.jar
```
**API Call Format:**
```bash
http://localhost:8080/api/recommend?userId=<userID>&latitude=<lat>&longitude=<lon>&localTime=<time>&dateFilter=<filter>&distanceFilter=<filter>
```

---

### 3. Set Up Notification System (Windows Service)
```bash
   cd notification-system
   cd "C:\Windows\Microsoft.NET\Framework\v4.0.30319"
   InstallUtil.exe "path-to-the-bin-folder/bin/Debug/NotificationModule.exe"
```
**Start the service:** Go to Windows Services and start "SendNotif".

---

### 4. Set Up User Management System & Task Organizer
```bash
   cd user-management
   npm install
   npm run server
```

---

## API Endpoints

| Method | Endpoint                     | Description          |
|--------|-----------------------------|----------------------|
| POST   | `/api/auth/register`        | Register User       |
| POST   | `/api/auth/login`           | Login User          |
| GET    | `/api/auth/logout`          | Logout User         |
| PUT    | `/api/auth/updatedetails`   | Update User Details |
| GET    | `/api/events`               | Get All Events      |
| GET    | `/api/events/:id`           | Get Individual Event |

---

## Environment Variables

Create a `.env` file and add:
```bash
PORT=8000
JWT_SECRET=your_jwt_secret
JWT_EXPIRE=30d
API_KEY=your_google_api_key
MODEL_NAME=models/text-bison-001
```
Ensure `servicekey.json` from Firebase is in the root directory.

---

## Features
- **Personalized Activity Recommendations** using weather & traffic data
- **Push Notifications** tailored to user location & preferences
- **Secure Authentication & Authorization** using JWT
- **Task & Event Management** for better productivity
- **MVC Architecture** ensuring modular and maintainable code

FocusMate provides a seamless experience for productivity and time management. 🚀

