# Task 2 — data_handler.py
# Saving and loading student data as a JSON file

import json
import os

# ── SAVE ──────────────────────────────────────────
def save_students(students):
    with open("students.json", "w") as f:
        json.dump(students, f, indent=2)
    print("Saved to students.json")


# ── LOAD ──────────────────────────────────────────
def load_students():
    if not os.path.exists("students.json"):
        print("No file found — returning empty list")
        return []
    with open("students.json", "r") as f:
        students = json.load(f)
    return students


# ── ADD ONE STUDENT ───────────────────────────────
def save_student(new_student):
    students = load_students()
    students.append(new_student)
    save_students(students)
    print("Added:", new_student["name"])


# ── TEST IT ───────────────────────────────────────
from students import students  # import your list from Task 1

# Save the whole list to a file
save_students(students)

# Load it back and print
loaded = load_students()
print("Loaded", len(loaded), "students")

# Add a brand new student
save_student({
    "id": "S106",
    "name": "Divya Nair",
    "room": 302,
    "floor": 3,
    "present": False
})

# Load again — should now show 6 students
loaded = load_students()
print("After adding — total:", len(loaded))
print("Last student:", loaded[-1]["name"])