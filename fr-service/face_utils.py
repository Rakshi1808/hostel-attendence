# face_utils.py
import os
import base64
import numpy as np
import cv2
from deepface import DeepFace
from config import PHOTOS_DIR, CONFIDENCE_THRESHOLD


# ── Load face detector once at startup ───────────────
face_detector = cv2.CascadeClassifier(
    cv2.data.haarcascades + "haarcascade_frontalface_default.xml"
)


# ── Custom exceptions ─────────────────────────────────
class NoFaceDetectedError(Exception):
    pass

class FaceMatchFailedError(Exception):
    pass

class StudentNotRegisteredError(Exception):
    pass


# ── Functions ─────────────────────────────────────────
def get_reference_photo(student_id):
    path = os.path.join(PHOTOS_DIR, student_id + ".jpg")
    if not os.path.exists(path):
        raise StudentNotRegisteredError(
            f"No photo for {student_id}"
        )
    return path


def decode_base64_image(base64_string):
    if "," in base64_string:
        base64_string = base64_string.split(",")[1]
    image_bytes = base64.b64decode(base64_string)
    np_array    = np.frombuffer(image_bytes, dtype=np.uint8)
    image       = cv2.imdecode(np_array, cv2.IMREAD_COLOR)
    if image is None:
        raise ValueError("Failed to decode image")
    return image


def has_face(image):
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


def verify_face(student_id, selfie_image):
    reference_photo = get_reference_photo(student_id)
    try:
        result = DeepFace.verify(
            img1_path      = reference_photo,
            img2_path      = selfie_image,
            model_name     = "ArcFace",
            enforce_detection = False
        )
        distance   = result["distance"]
        confidence = round(1 - distance, 4)

        if not result["verified"]:
            raise FaceMatchFailedError(
                f"Face does not match — confidence {confidence}"
            )

        return {"match": True, "confidence": confidence}

    except FaceMatchFailedError:
        raise                              # pass it through as-is

    except Exception as e:
        raise FaceMatchFailedError(f"Verification failed: {e}")