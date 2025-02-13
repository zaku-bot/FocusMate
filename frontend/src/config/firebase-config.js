// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyBwR2Y-mtajQszTzeZLk0a4ENFxjR9tZC8",
  authDomain: "fir-auth-7e9d6.firebaseapp.com",
  projectId: "fir-auth-7e9d6",
  storageBucket: "fir-auth-7e9d6.appspot.com",
  messagingSenderId: "425295413107",
  appId: "1:425295413107:web:e404f232c053b72cd24f06",
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export default app;
