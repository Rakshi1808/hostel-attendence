# Task 1 — students.py
# This is the data your FR service will work with

students = [
    {"id": "S101", "name": "Arjun Kumar",  "room": 214, "floor": 2, "present": False},
    {"id": "S102", "name": "Priya Sharma", "room": 312, "floor": 3, "present": True },
    {"id": "S103", "name": "Ravi Patel",   "room": 108, "floor": 1, "present": False},
    {"id": "S104", "name": "Sneha Reddy",  "room": 215, "floor": 2, "present": True },
    {"id": "S105", "name": "Karan Singh",  "room": 401, "floor": 4, "present": False},
]


def find_student(student_id):
    for s in students:
        if s["id"] == student_id:
            return s
    return None


def print_summary():
    for s in students:
        print(s["name"], "— Room", s["room"])


def count_present():
    count = 0
    for s in students:
        if s["present"]:
            count = count + 1
    return count


# Run this to test
print_summary()
print("Present today:", count_present())

found = find_student("S103")
if found:
    print("Found:", found["name"])