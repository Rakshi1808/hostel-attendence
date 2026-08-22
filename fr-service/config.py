# config.py
# All constants for the FR service in one place.
# Change a value here and it updates everywhere.

# folder where reference photos are saved after /register-face
PHOTOS_DIR = "uploads/faces"

# ArcFace cosine distance threshold
# distance < 0.68 = same person (verified)
# distance >= 0.68 = different person (rejected)
# lower value = stricter matching
CONFIDENCE_THRESHOLD = 0.68

# Flask server settings
PORT         = 5000
SERVICE_NAME = "fr-service"
VERSION      = "1.0.0"