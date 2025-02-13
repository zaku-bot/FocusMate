const jwt = require("jsonwebtoken");
const admin = require("firebase-admin");

const ErrorResponse = require("../utilities/errorResponse");

exports.protect = async (req, res, next) => {
  let token;

  if (
    req.headers.authorization &&
    req.headers.authorization.startsWith("Bearer")
  ) {
    token = req.headers.authorization.split(" ")[1];
  }

  if (!token) {
    return next(new ErrorResponse("Not authorized to access this route", 401));
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    const db = admin.firestore();

    console.log(decoded);

    const userRef = await db.collection("users").doc(decoded.id).get();
    if (!userRef._fieldsProto) {
      return next(new ErrorResponse("User Does not exist", 400));
    }

    req.uid = decoded.id;
    next();
  } catch (err) {
    return next(
      new ErrorResponse("Not authorized to access this route----", 401)
    );
  }
};
