# Focus Mate

**Description:**

The Guardian Angel app's core functionality revolves around seamless user interaction and data management. Users initiate actions through requests and commands, including registration, login, preferences updates, and event creation. The control flow and robust error handling mechanisms ensure systematic processing of user inputs, offering protection against invalid requests and unauthorized access.

Central to the app's security is the Authentication & Authorization Process. This component employs the MVC pattern, focusing on Controller Logic to manage user authentication, preferences, and event-related processes. The MVC architecture divides the application into Model, View, and Controller, with the Controller Logic orchestrating essential functionalities.

For effective data management, the component interacts with the Firebase/Firestore database, executing CRUD operations. This ensures secure and efficient access to user data, preferences, and events. The architecture guarantees a seamless flow of information, supporting the app's core features.


## Table of Contents

- [Installation](#installation)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [API Endpoints](#api-endpoints)
- [Environment Variables](#environment-variables)

## Installation

1. Clone the repository:

```bash
git clone https://github.com/your-username/your-project.git
cd your-project
```

2. Install Dependencies:

```bash
npm install
```

3. Run Server:

```bash
npm run server
```


## Features  

1. User Input (Requests/Commands):  
 Users interact with the Guardian Angel app by sending requests and commands, such as registering, logging in, updating preferences, and creating events.
2. Control Flow & Error Handling:  
 The control flow and error handling ensure that user inputs are processed systematically. It manages error scenarios, safeguarding against invalid requests and unauthorized access.
3. Authentication & Authorization Process:  
This component handles user authentication and authorization, ensuring secure access to the app. It follows the MVC pattern with a focus on Controller Logic, managing processes like user authentication and preferences.
4. MVC Pattern (Controller Logic):  
The MVC architecture divides the application into Model, View, and Controller. In this context, the Controller Logic manages user authentication, preferences, and event-related processes.
5. Data Access & Storage Ops:  
The component interacts with the Firebase/Firestore database, executing CRUD operations. It ensures secure and efficient access to user data, preferences, and events.
6. Response to User (Success/Failure):  
 Based on the processed requests, the component generates responses for users, indicating success or failure. This ensures a clear and informative interaction with users.


## Technologies Used  

1. Backend: Node.js and Express.js for API development.  
2. Database: Firebase/Firestore for efficient and scalable data storage.  
3. Hosting: EC2 instances for reliable hosting and scalability.

## API Endpoints

| PORT | Endpoint            | Description              |
| ------ | ------------------- | ------------------------ |
| POST    | `/api/auth/register`     | Register User        |
| POST   | `/api/auth/login`     | Login User    |
| Get   | `/api/auth/logout`     | Logout User    |
| PUT   | `/api/auth/updatedetails`     | Update User details    |
| GET    | `/api/events`     | Get All events |
| GET | `/api/events/:id`     | Get individual event       |




## Environment Variables

1. Generate your servicekey.json from firebase from firestore and place it in the root directory.
2. Create a config folder in the root directory and then create a config.env file inside the folder.
3. Update the following variables with your secret keys.

| PORT | 8000            |
| ------ | ------------------- | 
| JWT_SECRET    | Any combination of numbers and alphabet to encrypt the token    | 
| JWT_EXPIRE   | 30d    |
| API_KEY   | Google's Palm Generative AI secret key    |
| MODEL_NAME   | models/text-bison-001    | 





