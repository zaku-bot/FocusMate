const express = require("express");
const {
  register,
  login,
  logout,
  getMe,
  updatePreferences,
} = require("../controllers/auth");

const { protect } = require("../middleware/middleware");

const router = express.Router();

router.post("/register", register);
router.post("/login", login);
router.get("/logout", logout);
router.put("/updatedetails", protect, updatePreferences);
router.get("/me", protect, getMe);

module.exports = router;
