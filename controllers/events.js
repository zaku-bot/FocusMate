const admin = require("firebase-admin");
const { TextServiceClient } = require("@google-ai/generativelanguage").v1beta2;
const { GoogleAuth } = require("google-auth-library");

const ErrorResponse = require("../utilities/errorResponse");
const basePrompt = require("../utilities/prompt");

exports.getEvents = async (req, res, next) => {
  try {
    const db = admin.firestore();
    const eventsRef = db.collection("events");
    eventsRef.get().then((querySnapshot) => {
      const documents = [];
      querySnapshot.forEach((doc) => {
        documents.push({
          id: doc.id,
          data: doc.data(),
        });
      });
      res.status(200).json({ success: true, data: documents });
    });
  } catch (error) {
    console.log(error);
    return next(new ErrorResponse("Internal Server Error", 500));
  }
};

exports.getEvent = async (req, res, next) => {
  try {
    const db = admin.firestore();
    const eventsRef = db.collection("events").doc(req.params.id);
    eventsRef.get().then((doc) => {
      if (doc.exists) {
        res.json({ success: true, id: doc.id, data: doc.data() });
      } else {
        res.status(404).json({ error: "Document not found" });
      }
    });
  } catch (error) {
    console.log(error);
    return next(new ErrorResponse("Internal Server Error", 500));
  }
};

exports.seedEvents = async (req, res, next) => {
  try {
    const db = admin.firestore();
    // const uid = req.uid;
    const uid = "H6KY1R3hJkhfHRiclKnZ4gb7yRs1";
    const eventsArray = [
      "Sports",
      "Concerts",
      "Comedy Shows",
      "Night Clubs",
      "Restaurants",
      "Movies",
    ];

    const eventsPromises = eventsArray.map(async (event) => {
      return getUserPreferenceEvents(event);
    });

    var events = await Promise.all(eventsPromises);
    events = events.flat();

    const formatEvents = await formatEventData(events);

    await Promise.all(
      formatEvents.map(async (event) => {
        await db.collection("events").add(event);
      })
    );

    res.status(200).json({ success: true, data: formatEvents });
  } catch (error) {
    console.log(error);
    return next(new ErrorResponse("Internal Server Error", 500));
  }
};

const getUserPreferenceEvents = async (preference) => {
  const MODEL_NAME = process.env.MODEL_NAME;
  const API_KEY = process.env.API_KEY;

  const client = new TextServiceClient({
    authClient: new GoogleAuth().fromAPIKey(API_KEY),
  });

  const result = await client.generateText({
    model: MODEL_NAME,
    prompt: {
      text: basePrompt(preference),
    },
  });
  let output = result[0].candidates[0].output;

  if (/^```json/.test(output)) {
    output = output.replace(/^```json\s*/, "");
  }

  // Check if the string ends with "```"
  if (/```$/.test(output)) {
    output = output.replace(/\s*```$/, "");
  }
  return JSON.parse(output);
};

const formatEventData = async (events) => {
  const formattedEvents = events.map(async (d) => {
    d["eventType"] = d["Event Type"];
    delete d["Event Type"];

    d["eventDate"] = d["Event Date"];
    delete d["Event Date"];

    d["eventLocationName"] = d["Event Location"];
    delete d["Event Location"];

    d["eventName"] = d["Event Name"];
    delete d["Event Name"];

    d["eventTime"] = d["Event Time"];
    delete d["Event Time"];

    d["additionalDetails"] = d["Additional Details"];
    d["additionalDetails"]["cost"] = d["Additional Details"]["Cost"];
    d["additionalDetails"]["bookingLink"] =
      d["Additional Details"]["Booking Link"];
    delete d["additionalDetails"]["Cost"];
    delete d["additionalDetails"]["Booking Link"];
    delete d["Additional Details"];

    const locationName = d["eventLocationName"];
    const apiKey = process.env.MAPS_KEY;
    const apiUrl = `https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(
      locationName
    )}&key=${apiKey}`;

    let coordinates = {};
    const response = await fetch(apiUrl);
    const data = await response.json();

    if (data.results.length > 0) {
      const location = data.results[0].geometry.location;
      coordinates = {
        lat: location.lat,
        lng: location.lng,
      };
    } else {
      coordinates = {
        lat: null,
        lng: null,
      };
    }
    d["eventLocation"] = {
      latitude: coordinates.lat,
      longitude: coordinates.lng,
    };

    return d;
  });

  return await Promise.all(formattedEvents);
};
