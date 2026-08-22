# face_utils.py
import os
import json
import numpy as np
import cv2
from deepface import DeepFace
from config import PHOTOS_DIR, CONFIDENCE_THRESHOLD


# ── Load face detector once at startup ───────────────────
face_detector = cv2.CascadeClassifier(
    cv2.data.haarcascades + "haarcascade_frontalface_default.xml"
)


# ── Custom exceptions ─────────────────────────────────────
class NoFaceDetectedError(Exception):
    pass

class FaceMatchFailedError(Exception):
    def __init__(self, message, confidence=0.0):
        super().__init__(message)
        self.confidence = confidence  # so app.py can include it in response


# ── Image helpers ─────────────────────────────────────────
def decode_image_file(file_storage):
    """
    Converts a Flask FileStorage object (uploaded photo)
    into an OpenCV image (numpy array).

    Called in both /register-face and /verify-face.

    Args:
        file_storage — werkzeug FileStorage from request.files["photo"]

    Returns:
        cv2 image (numpy array, BGR colour)

    Raises:
        ValueError — if file cannot be decoded as an image
    """
    if file_storage is None:
        raise ValueError("No file provided")

    # read raw bytes from uploaded file
    file_bytes = file_storage.read()
    if not file_bytes:
        raise ValueError("Uploaded file is empty")

    # decode bytes → numpy array → cv2 image
    np_array = np.frombuffer(file_bytes, dtype=np.uint8)
    image    = cv2.imdecode(np_array, cv2.IMREAD_COLOR)

    if image is None:
        raise ValueError("Could not decode image — must be a valid JPEG or PNG file")

    return image


def has_face(image):
    """
    Returns True if at least one face is detected in the image.
    Uses Haar Cascade — fast pre-check before running heavy DeepFace.

    Raises:
        NoFaceDetectedError — if no face found
    """
    if image is None:
        raise ValueError("Image is None")

    gray  = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    faces = face_detector.detectMultiScale(
        gray,
        scaleFactor=1.1,
        minNeighbors=5
    )

    if len(faces) == 0:
        raise NoFaceDetectedError("No face detected in image")

    return True


# ── Embedding extraction ──────────────────────────────────
def extract_embedding(image):
    """
    Extracts a 512-dimensional ArcFace face embedding from an image.

    Called during POST /register-face.
    The returned list is sent to Spring Boot which saves it
    as JSON in the face_embeddings table.

    Args:
        image — cv2 image (numpy array)

    Returns:
        list[float] — 512 floats representing the face

    Raises:
        NoFaceDetectedError — if DeepFace finds no face
        ValueError          — if extraction fails
    """
    try:
        representations = DeepFace.represent(
            img_path          = image,
            model_name        = "ArcFace",
            enforce_detection = True,
            detector_backend  = "opencv"
        )

        if not representations:
            raise NoFaceDetectedError("DeepFace found no face in the image")

        # return the embedding of the most prominent face
        return representations[0]["embedding"]

    except NoFaceDetectedError:
        raise

    except Exception as e:
        raise ValueError(f"Embedding extraction failed: {e}")


# ── Face verification ─────────────────────────────────────
def verify_face_from_embedding(selfie_image, stored_embedding_list):
    """
    Compares a selfie (cv2 image) against a stored embedding
    (list of floats loaded from face_embeddings table in MySQL).

    Called during POST /verify-face.
    Spring Boot loads the embedding from MySQL and sends it here.
    Flask computes cosine similarity and returns the result.

    Args:
        selfie_image         — cv2 image from decode_image_file()
        stored_embedding_list — list[float] parsed from Spring Boot's JSON string

    Returns:
        dict: { "match": True, "confidence": float }

    Raises:
        FaceMatchFailedError — if cosine similarity is below threshold
        NoFaceDetectedError  — if no face in selfie
    """
    try:
        # extract embedding from the live selfie
        selfie_representations = DeepFace.represent(
            img_path          = selfie_image,
            model_name        = "ArcFace",
            enforce_detection = False,
            detector_backend  = "opencv"
        )

        if not selfie_representations:
            raise NoFaceDetectedError("No face detected in selfie")

        selfie_embedding = np.array(selfie_representations[0]["embedding"])
        stored_embedding = np.array(stored_embedding_list)

        # cosine similarity — ArcFace: similarity > 0.32 means same person
        # (distance < 0.68 in cosine distance terms)
        dot_product = np.dot(selfie_embedding, stored_embedding)
        norm_a      = np.linalg.norm(selfie_embedding)
        norm_b      = np.linalg.norm(stored_embedding)
        cosine_sim  = float(dot_product / (norm_a * norm_b))
        confidence  = round(cosine_sim, 4)

        # distance = 1 - cosine_sim
        # ArcFace threshold: distance < 0.68 → verified
        distance = 1 - cosine_sim
        verified = distance < CONFIDENCE_THRESHOLD   # 0.68 in config.py

        if not verified:
            raise FaceMatchFailedError(
                f"Face does not match — confidence {confidence}",
                confidence=confidence
            )

        return {"match": True, "confidence": confidence}

    except (FaceMatchFailedError, NoFaceDetectedError):
        raise

    except Exception as e:
        raise FaceMatchFailedError(
            f"Verification error: {e}",
            confidence=0.0
        )