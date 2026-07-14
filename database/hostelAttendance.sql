CREATE DATABASE  IF NOT EXISTS `hostel_attendance` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `hostel_attendance`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: hostel_attendance
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
/*!40000 ALTER TABLE `admins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance_logs`
--

DROP TABLE IF EXISTS `attendance_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `roll_no` varchar(15) NOT NULL,
  `session_id` int NOT NULL,
  `marked_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `confidence` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `ip_address` varchar(50) DEFAULT NULL,
  `marked_by` enum('STUDENT','WARDEN','ADMIN') NOT NULL DEFAULT 'STUDENT',
  PRIMARY KEY (`id`),
  UNIQUE KEY `one_per_session` (`roll_no`,`session_id`),
  KEY `idx_marked_at` (`marked_at`),
  KEY `idx_al_session_id` (`session_id`),
  CONSTRAINT `attendance_logs_ibfk_1` FOREIGN KEY (`roll_no`) REFERENCES `students` (`roll_no`) ON DELETE CASCADE,
  CONSTRAINT `attendance_logs_ibfk_2` FOREIGN KEY (`session_id`) REFERENCES `attendance_sessions` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `attendance_logs_chk_1` CHECK (((`confidence` is null) or ((`confidence` >= 0) and (`confidence` <= 1))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance_logs`
--

LOCK TABLES `attendance_logs` WRITE;
/*!40000 ALTER TABLE `attendance_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendance_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance_sessions`
--

DROP TABLE IF EXISTS `attendance_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance_sessions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `session_date` date NOT NULL,
  `window_start` time NOT NULL DEFAULT '08:30:00',
  `window_end` time NOT NULL DEFAULT '09:30:00',
  `hostel_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_session` (`session_date`,`hostel_id`,`window_start`),
  KEY `hostel_id` (`hostel_id`),
  CONSTRAINT `attendance_sessions_ibfk_1` FOREIGN KEY (`hostel_id`) REFERENCES `hostels` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `attendance_sessions_chk_1` CHECK ((`window_start` < `window_end`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance_sessions`
--

LOCK TABLES `attendance_sessions` WRITE;
/*!40000 ALTER TABLE `attendance_sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendance_sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `face_embeddings`
--

DROP TABLE IF EXISTS `face_embeddings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `face_embeddings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `roll_no` varchar(15) NOT NULL,
  `photo_path` varchar(255) NOT NULL,
  `embedding` json NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `registered_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `registered_by` enum('WARDEN','ADMIN') NOT NULL DEFAULT 'WARDEN',
  `deactivated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_embedding_active` (`roll_no`,`is_active`),
  CONSTRAINT `face_embeddings_ibfk_1` FOREIGN KEY (`roll_no`) REFERENCES `students` (`roll_no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `face_embeddings`
--

LOCK TABLES `face_embeddings` WRITE;
/*!40000 ALTER TABLE `face_embeddings` DISABLE KEYS */;
/*!40000 ALTER TABLE `face_embeddings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hostels`
--

DROP TABLE IF EXISTS `hostels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hostels` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `gender` enum('BOYS','GIRLS') DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hostels`
--

LOCK TABLES `hostels` WRITE;
/*!40000 ALTER TABLE `hostels` DISABLE KEYS */;
INSERT INTO `hostels` VALUES (1,'Kalpana Chawla','GIRLS',1,0,0),(2,'Bhagirathi','GIRLS',1,0,0),(3,'Cauvery','GIRLS',1,0,0),(4,'Alaknanda','GIRLS',1,0,0);
/*!40000 ALTER TABLE `hostels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parent_contacts`
--

DROP TABLE IF EXISTS `parent_contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parent_contacts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `roll_no` varchar(15) NOT NULL,
  `parent_name` varchar(100) NOT NULL,
  `parent_phone` varchar(15) NOT NULL,
  `relation` enum('FATHER','MOTHER','GUARDIAN') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `roll_no` (`roll_no`),
  CONSTRAINT `parent_contacts_ibfk_1` FOREIGN KEY (`roll_no`) REFERENCES `students` (`roll_no`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parent_contacts`
--

LOCK TABLES `parent_contacts` WRITE;
/*!40000 ALTER TABLE `parent_contacts` DISABLE KEYS */;
INSERT INTO `parent_contacts` VALUES (1,'525410037','Ravinder Goud','9876543210','FATHER');
/*!40000 ALTER TABLE `parent_contacts` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `check_parent_phone` BEFORE INSERT ON `parent_contacts` FOR EACH ROW BEGIN
    DECLARE s_phone VARCHAR(15);
    SELECT student_phone INTO s_phone
    FROM   students
    WHERE  roll_no = NEW.roll_no;

    IF NEW.parent_phone = s_phone THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Parent phone cannot be same as student phone';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `programmes`
--

DROP TABLE IF EXISTS `programmes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `programmes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `programme_name` varchar(50) NOT NULL,
  `total_years` int NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `programme_name` (`programme_name`),
  CONSTRAINT `programmes_chk_1` CHECK ((`total_years` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `programmes`
--

LOCK TABLES `programmes` WRITE;
/*!40000 ALTER TABLE `programmes` DISABLE KEYS */;
INSERT INTO `programmes` VALUES (1,'MCA',3,1),(2,'BCA',3,1),(3,'BTech',4,1);
/*!40000 ALTER TABLE `programmes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `id` int NOT NULL AUTO_INCREMENT,
  `hostel_id` int NOT NULL,
  `room_no` varchar(20) NOT NULL,
  `floor_no` int DEFAULT NULL,
  `block` varchar(20) DEFAULT NULL,
  `capacity` int NOT NULL DEFAULT '1',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_room` (`hostel_id`,`room_no`),
  CONSTRAINT `rooms_ibfk_1` FOREIGN KEY (`hostel_id`) REFERENCES `hostels` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `rooms_chk_1` CHECK ((`capacity` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES (101,1,'1',1,'A',1,1),(102,1,'2',1,'A',1,1),(103,1,'3',1,'A',1,1),(104,1,'4',1,'A',1,1),(105,1,'5',1,'A',1,1),(106,1,'6',1,'A',1,1),(107,1,'7',1,'A',1,1),(108,1,'8',1,'A',1,1),(109,1,'9',1,'A',1,1),(110,1,'10',1,'A',1,1),(111,1,'11',2,'B',1,1),(112,1,'12',2,'B',1,1),(113,1,'13',2,'B',1,1),(114,1,'14',2,'B',1,1),(115,1,'15',2,'B',1,1),(116,1,'16',2,'B',1,1),(117,1,'17',2,'B',1,1),(118,1,'18',2,'B',1,1),(119,1,'19',2,'B',1,1),(120,1,'20',2,'B',1,1),(121,2,'21',1,'A',1,1),(122,2,'22',1,'A',1,1),(123,2,'23',1,'A',1,1),(124,2,'24',1,'A',1,1),(125,2,'25',1,'A',1,1),(126,2,'26',1,'A',1,1),(127,2,'27',1,'A',1,1),(128,2,'28',1,'A',1,1),(129,2,'29',1,'A',1,1),(130,2,'30',1,'A',1,1),(131,2,'31',2,'B',1,1),(132,2,'32',2,'B',1,1),(133,2,'33',2,'B',1,1),(134,2,'34',2,'B',1,1),(135,2,'35',2,'B',1,1),(136,2,'36',2,'B',1,1),(137,2,'37',2,'B',1,1),(138,2,'38',2,'B',1,1),(139,2,'39',2,'B',1,1),(140,2,'40',2,'B',1,1);
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_activations`
--

DROP TABLE IF EXISTS `student_activations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_activations` (
  `roll_no` varchar(15) NOT NULL,
  `activation_code` varchar(6) NOT NULL,
  `expires_at` timestamp NOT NULL,
  `activated` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`roll_no`),
  CONSTRAINT `student_activations_ibfk_1` FOREIGN KEY (`roll_no`) REFERENCES `students` (`roll_no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_activations`
--

LOCK TABLES `student_activations` WRITE;
/*!40000 ALTER TABLE `student_activations` DISABLE KEYS */;
INSERT INTO `student_activations` VALUES ('525410036','573405','2026-07-03 14:12:58',1),('525410037','804202','2026-06-28 07:09:27',1);
/*!40000 ALTER TABLE `student_activations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `roll_no` varchar(15) NOT NULL,
  `name` varchar(100) NOT NULL,
  `personal_email` varchar(100) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `programme_id` int NOT NULL,
  `year_of_study` int NOT NULL,
  `room_id` int DEFAULT NULL,
  `student_phone` varchar(15) NOT NULL,
  `account_status` enum('PENDING','PROFILE_PENDING','FACE_PENDING','FACE_APPROVAL_PENDING','ACTIVE','SUSPENDED') DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`roll_no`),
  UNIQUE KEY `student_phone` (`student_phone`),
  UNIQUE KEY `personal_email` (`personal_email`),
  KEY `programme_id` (`programme_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `students_ibfk_1` FOREIGN KEY (`programme_id`) REFERENCES `programmes` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `students_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `students_chk_1` CHECK ((`year_of_study` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES ('525410036','Toshib','toshibansari3905@gmail.com','$2a$10$6e8iGF3A/saC.Vb2NV7blOz2WRE/uM0fbyHnhEOKJzHbfAKwBQewC',1,3,NULL,'8899246644','PROFILE_PENDING','2026-07-03 14:01:47','2026-07-03 14:06:10'),('525410037','RAKSHITA','Toshibansari','$2a$10$siCC6I90dErL1xDobkVS..LOtHOdI4elr2/k3U4/z/HqzT47DOVYa',1,2,101,'8630233455','FACE_PENDING','2026-06-27 05:46:55','2026-07-07 18:15:35');
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wardens`
--

DROP TABLE IF EXISTS `wardens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wardens` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `hostel_id` int NOT NULL,
  `role` enum('MAIN_WARDEN','ASSISTANT_WARDEN') NOT NULL,
  `phone` varchar(15) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`),
  KEY `hostel_id` (`hostel_id`),
  CONSTRAINT `wardens_ibfk_1` FOREIGN KEY (`hostel_id`) REFERENCES `hostels` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wardens`
--

LOCK TABLES `wardens` WRITE;
/*!40000 ALTER TABLE `wardens` DISABLE KEYS */;
/*!40000 ALTER TABLE `wardens` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-14 17:58:43
