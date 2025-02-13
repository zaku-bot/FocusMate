const express = require("express");
const { getEvents, getEvent, seedEvents } = require("../controllers/events");

const { protect } = require("../middleware/middleware");

const router = express.Router();

router.get("/", getEvents);
router.get("/:id", getEvent);
router.post("/", seedEvents);

module.exports = router;
