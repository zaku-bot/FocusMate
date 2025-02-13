const express = require("express");
const admin = require("firebase-admin");
const credential = require("./servicekey.json");
const dotenv = require("dotenv");

const auth = require("./routes/auth");
const events = require("./routes/events");
const errorHandler = require("./middleware/error");

dotenv.config({ path: "./config/config.env" });

admin.initializeApp({
  credential: admin.credential.cert(credential),
});

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use("/api/auth", auth);
app.use("/api/events", events);
app.use(errorHandler);

const PORT = process.env.PORT || 8000;

app.listen(PORT, () => {
  console.log(`Server running on PORT: ${PORT}`);
});
