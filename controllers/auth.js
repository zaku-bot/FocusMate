const bcrypt = require("bcryptjs");
const admin = require("firebase-admin");
const ErrorResponse = require("../utilities/errorResponse");
const { getSignedJwtToken } = require("../utilities");

exports.register = async (req, res, next) => {
  try {
    const db = admin.firestore();
    const uid = req.body.uid;
    const salt = await bcrypt.genSalt(10);
    const password = await bcrypt.hash(uid, salt);
    const preferences = req.body.preferences.split(",");
    const user = {
      name: req.body.displayName,
      email: req.body.email,
      photoURL: req.body.photoURL,
      password,
      preferences,
    };

    await db.collection("users").doc(uid).set(user);

    const userRef = await db.collection("users").doc(uid).get();
    const userPref = userRef._fieldsProto.preferences.arrayValue.values;

    const userData = {
      name: userRef._fieldsProto.name.stringValue,
      email: userRef._fieldsProto.email.stringValue,
      photoURL: userRef._fieldsProto.photoURL.stringValue,
      preferences: userPref,
    };

    const token = getSignedJwtToken(uid);
    res.status(200).json({ success: true, token, data: userData });
  } catch (error) {
    console.log(error);
    return next(new ErrorResponse("Internal Server Error", 500));
  }
};

exports.login = async (req, res, next) => {
  try {
    const db = admin.firestore();
    const uid = req.body.uid;

    const userRef = await db.collection("users").doc(uid).get();
    if (!userRef._fieldsProto) {
      return next(new ErrorResponse("User Does not exist", 400));
    }

    console.log(userRef);

    const userPref = userRef._fieldsProto.preferences.arrayValue.values;
    const userData = {
      name: userRef._fieldsProto.name.stringValue,
      email: userRef._fieldsProto.email.stringValue,
      photoURL: userRef._fieldsProto.photoURL.stringValue,
      preferences: userPref,
    };
    const token = getSignedJwtToken(uid);
    res.status(200).json({ success: true, token, data: userData });
  } catch (error) {
    console.log(error);
    return next(new ErrorResponse("Internal Server Error", 500));
  }
};

exports.logout = async (req, res, next) => {
  res.status(200).json({ success: true, data: {} });
};

exports.updatePreferences = async (req, res, next) => {
  try {
    const db = admin.firestore();
    const uid = req.uid;
    const preferences = req.body.preferences.split(",");

    let userRef = await db.collection("users").doc(uid).update({
      preferences,
    });

    userRef = await db.collection("users").doc(uid).get();
    const userPref = userRef._fieldsProto.preferences.arrayValue.values;

    const userData = {
      name: userRef._fieldsProto.name.stringValue,
      email: userRef._fieldsProto.email.stringValue,
      photoURL: userRef._fieldsProto.photoURL.stringValue,
      preferences: userPref,
    };
    res.status(200).json({ success: true, data: userData });
  } catch (error) {
    console.log(error);
    return next(new ErrorResponse("Internal Server Error", 500));
  }
};

exports.getMe = async (req, res, next) => {
  try {
    const db = admin.firestore();
    const uid = req.uid;

    const userRef = await db.collection("users").doc(uid).get();
    const userPref = userRef._fieldsProto.preferences.arrayValue.values;

    const userData = {
      name: userRef._fieldsProto.name.stringValue,
      email: userRef._fieldsProto.email.stringValue,
      photoURL: userRef._fieldsProto.photoURL.stringValue,
      preferences: userPref,
    };
    res.status(200).json({ success: true, data: userData });
  } catch (error) {
    console.log(error);
    return next(new ErrorResponse("Internal Server Error", 500));
  }
};
