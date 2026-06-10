# app.py — top of file
import os
import logging
from flask import Flask, jsonify, request
from flask_cors import CORS
from config import PORT, SERVICE_NAME, VERSION, PHOTOS_DIR
from face_utils import (
    get_reference_photo,
    decode_base64_image,
    has_face,
    verify_face,
    NoFaceDetectedError,
    FaceMatchFailedError,
    StudentNotRegisteredError
)


# ── Logging setup ─────────────────────────────────────
logging.basicConfig(
    level   = logging.INFO,
    format  = "%(asctime)s  %(levelname)s  %(message)s",
    handlers= [
        logging.FileHandler("fr_service.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)


# ── Flask app ─────────────────────────────────────────
app = Flask(__name__)
CORS(app, origins=["http://localhost:8080"])


# ── Route 1: Health check ─────────────────────────────
@app.route("/health", methods=["GET"])
def health():
    logger.info("Health check called")
    return jsonify({
        "status":  "ok",
        "service": SERVICE_NAME,
        "version": VERSION
    })


# ── Route 2: Verify face ──────────────────────────────
@app.route("/verify", methods=["POST"])
def verify():

    data = request.json

    # Gate 1 — are required fields present?
    if not data or "student_id" not in data or "image_base64" not in data:
        logger.warning("Missing fields in request")
        return jsonify({"error": "missing_fields"}), 400

    student_id   = data["student_id"]
    image_base64 = data["image_base64"]

    logger.info(f"Verification attempt — student: {student_id}")

    try:
        # Gate 2 — does this student have a reference photo?
        photo_path = get_reference_photo(student_id)

        # Gate 3 — decode image and check a face exists
        image = decode_base64_image(image_base64)
        has_face(image)

        # Gate 4 — does the face match?
        result = verify_face(student_id, image)

        # All gates passed
        logger.info(f"Verified — student: {student_id} confidence: {result['confidence']}")
        return jsonify(result), 200

    except StudentNotRegisteredError as e:
        logger.error(f"Not registered — {student_id}: {e}")
        return jsonify({"error": "student_not_registered"}), 404

    except NoFaceDetectedError as e:
        logger.error(f"No face — {student_id}: {e}")
        return jsonify({"error": "no_face_detected"}), 400

    except FaceMatchFailedError as e:
        logger.error(f"Match failed — {student_id}: {e}")
        return jsonify({"error": "face_match_failed"}), 401

    except Exception as e:
        logger.error(f"Unexpected error — {student_id}: {e}")
        return jsonify({"error": "server_error"}), 500


@app.route("/register", methods=["POST"])
def register():
    if "student_id" not in request.form or "photo" not in request.files:
        logger.warning("Register called with missing fields")
        return jsonify({"error": "missing_fields"}), 400

    student_id = request.form["student_id"]
    photo      = request.files["photo"]
    photo_path = os.path.join(PHOTOS_DIR, student_id + ".jpg")

    photo.save(photo_path)

    logger.info(f"Registered student: {student_id}")
    return jsonify({
        "message":    f"Student {student_id} registered",
        "photo_path": photo_path
    }), 200

# ── Route 4: Check if student is registered ───────────
@app.route("/student/<student_id>", methods=["GET"])
def student_exists(student_id):
    photo_path = os.path.join(PHOTOS_DIR, student_id + ".jpg")
    exists     = os.path.exists(photo_path)

    logger.info(f"Student check — {student_id}: {exists}")
    return jsonify({
        "student_id": student_id,
        "registered": exists
    })


# ── Global error handlers ─────────────────────────────
@app.errorhandler(404)
def not_found(e):
    return jsonify({"error": "route_not_found"}), 404


@app.errorhandler(500)
def server_error(e):
    logger.error(f"Server error: {e}")
    return jsonify({"error": "server_error"}), 500


if __name__ == "__main__":
    logger.info(f"Starting {SERVICE_NAME} on port {PORT}")
    app.run(port=PORT, debug=True)