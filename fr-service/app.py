# app.py
# =========================================================
# Smart Hostel Attendance — Face Recognition Service
# =========================================================
#
# POST /register-face   multipart/form-data
#   receives: rollNo (str), photo (file)
#   returns:  { success, message, photoPath, embedding }
#
# POST /verify-face     multipart/form-data
#   receives: rollNo (str), photo (file), embedding (str → parsed as list)
#   returns:  { success, message, confidence }
#
# GET  /health
#   returns:  { status, service, version }
# =========================================================

import os
import json
import logging
import numpy as np
import cv2

from flask import Flask, jsonify, request
from flask_cors import CORS
from config import PORT, SERVICE_NAME, VERSION, PHOTOS_DIR
from face_utils import (
    decode_image_file,
    has_face,
    extract_embedding,
    verify_face_from_embedding,
    NoFaceDetectedError,
    FaceMatchFailedError,
)


# ── Logging ───────────────────────────────────────────────
logging.basicConfig(
    level   = logging.INFO,
    format  = "%(asctime)s  %(levelname)s  %(message)s",
    handlers=[
        logging.FileHandler("fr_service.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)


# ── Flask app ─────────────────────────────────────────────
app = Flask(__name__)
CORS(app, origins=["http://localhost:8080"])


# ─────────────────────────────────────────────────────────
# Route 1 — Health check
# GET /health
# ─────────────────────────────────────────────────────────
@app.route("/health", methods=["GET"])
def health():
    logger.info("Health check called")
    return jsonify({
        "status":  "ok",
        "service": SERVICE_NAME,
        "version": VERSION
    })


# ─────────────────────────────────────────────────────────
# Route 2 — Register face
# POST /register-face
# Content-Type: multipart/form-data
#
# Receives from Spring Boot:
#   rollNo  — student roll number (form field, string)
#   photo   — face image file (file upload)
#
# Returns to Spring Boot:
#   {
#     "success":   true,
#     "message":   "Face registered successfully",
#     "photoPath": "uploads/faces/525410037.jpg",
#     "embedding": [0.123, 0.456, ...]
#   }
#
# On failure:
#   { "success": false, "message": "reason here" }
# ─────────────────────────────────────────────────────────
@app.route("/register-face", methods=["POST"])
def register_face():

    # validate fields
    if "rollNo" not in request.form or "photo" not in request.files:
        logger.warning("POST /register-face — missing rollNo or photo")
        return jsonify({
            "success": False,
            "message": "Missing required fields: rollNo and photo"
        }), 400

    roll_no    = request.form["rollNo"]
    photo_file = request.files["photo"]

    logger.info(f"Face registration started — student: {roll_no}")

    try:
        # Step 1 — read uploaded file → OpenCV image
        image = decode_image_file(photo_file)

        # Step 2 — check face is present in photo
        has_face(image)

        # Step 3 — save photo to uploads/faces/
        os.makedirs(PHOTOS_DIR, exist_ok=True)
        photo_path = os.path.join(PHOTOS_DIR, roll_no + ".jpg")
        cv2.imwrite(photo_path, image)

        # Step 4 — extract ArcFace embedding vector
        embedding = extract_embedding(image)

        logger.info(
            f"Face registration ✓ — student: {roll_no} "
            f"embedding dimensions: {len(embedding)}"
        )

        # Spring Boot saves photoPath + embedding to face_embeddings table
        return jsonify({
            "success":   True,
            "message":   "Face registered successfully",
            "photoPath": photo_path,
            "embedding": embedding        # list of floats — Spring saves to DB
        }), 200

    except NoFaceDetectedError:
        logger.error(f"No face detected — {roll_no}")
        return jsonify({
            "success": False,
            "message": "No face detected in the photo — use a clear front-facing photo"
        }), 400

    except ValueError as e:
        logger.error(f"Invalid image — {roll_no}: {e}")
        return jsonify({
            "success": False,
            "message": f"Invalid image: {e}"
        }), 400

    except Exception as e:
        logger.error(f"Registration failed — {roll_no}: {e}")
        return jsonify({
            "success": False,
            "message": f"Registration failed: {e}"
        }), 500


# ─────────────────────────────────────────────────────────
# Route 3 — Verify face
# POST /verify-face
# Content-Type: multipart/form-data
#
# Receives from Spring Boot:
#   rollNo    — student roll number (form field, string)
#   photo     — current selfie (file upload)
#   embedding — previously saved embedding from DB (form field, JSON string)
#               Spring Boot sends it as:  "[0.123, 0.456, ...]"
#
# Spring Boot has already done before calling this:
#   ✓ JWT validation
#   ✓ Time window check (08:30 – 09:30)
#   ✓ WiFi / IP check
#   ✓ Loaded embedding from face_embeddings table
#
# Flask's only job: does this selfie match this embedding?
#
# Returns to Spring Boot:
#   { "success": true,  "message": "Face verification completed", "confidence": 0.91 }
#   { "success": false, "message": "Face does not match",         "confidence": 0.32 }
#
# Spring Boot then decides:
#   confidence >= threshold → mark attendance
#   confidence <  threshold → reject
# ─────────────────────────────────────────────────────────
@app.route("/verify-face", methods=["POST"])
def verify_face():

    # validate all three fields are present
    if (    "rollNo"    not in request.form
         or "photo"     not in request.files
         or "embedding" not in request.form):
        logger.warning("POST /verify-face — missing rollNo, photo, or embedding")
        return jsonify({
            "success": False,
            "message": "Missing required fields: rollNo, photo, and embedding",
            "confidence": 0.0
        }), 400

    roll_no          = request.form["rollNo"]
    photo_file       = request.files["photo"]
    embedding_string = request.form["embedding"]

    logger.info(f"Face verification started — student: {roll_no}")

    try:
        # Step 1 — parse embedding JSON string → list of floats
        # Spring Boot sends the embedding as a JSON array string
        stored_embedding = json.loads(embedding_string)

        # Step 2 — decode selfie file → OpenCV image
        image = decode_image_file(photo_file)

        # Step 3 — check face is present in selfie
        has_face(image)

        # Step 4 — compare selfie against stored embedding
        result = verify_face_from_embedding(image, stored_embedding)

        # result = { "match": True, "confidence": 0.91 }
        confidence = result["confidence"]

        logger.info(
            f"Face verification ✓ — student: {roll_no} "
            f"confidence: {confidence}"
        )

        return jsonify({
            "success":    True,
            "message":    "Face verification completed",
            "confidence": confidence
        }), 200

    except FaceMatchFailedError as e:
        # face detected but doesn't match — return confidence so Spring can log it
        # extract confidence from error if available, else 0.0
        confidence = getattr(e, "confidence", 0.0)
        logger.warning(f"Face match failed — {roll_no} confidence: {confidence}")
        return jsonify({
            "success":    False,
            "message":    "Face does not match",
            "confidence": confidence
        }), 200    # 200 so Spring Boot reads the body — it decides what to do

    except NoFaceDetectedError:
        logger.error(f"No face in selfie — {roll_no}")
        return jsonify({
            "success":    False,
            "message":    "No face detected in the selfie — look directly at the camera",
            "confidence": 0.0
        }), 400

    except ValueError as e:
        logger.error(f"Invalid image — {roll_no}: {e}")
        return jsonify({
            "success":    False,
            "message":    f"Invalid image: {e}",
            "confidence": 0.0
        }), 400

    except json.JSONDecodeError:
        logger.error(f"Invalid embedding string — {roll_no}")
        return jsonify({
            "success":    False,
            "message":    "Invalid embedding format — must be a JSON array",
            "confidence": 0.0
        }), 400

    except Exception as e:
        logger.error(f"Verification error — {roll_no}: {e}")
        return jsonify({
            "success":    False,
            "message":    f"Verification failed: {e}",
            "confidence": 0.0
        }), 500


# ── Global error handlers ─────────────────────────────────
@app.errorhandler(404)
def not_found(e):
    return jsonify({"success": False, "message": "Route not found"}), 404

@app.errorhandler(500)
def server_error(e):
    logger.error(f"Server error: {e}")
    return jsonify({"success": False, "message": "Internal server error"}), 500


if __name__ == "__main__":
    os.makedirs(PHOTOS_DIR, exist_ok=True)
    logger.info(f"Starting {SERVICE_NAME} on port {PORT}")
    app.run(port=PORT, debug=True)