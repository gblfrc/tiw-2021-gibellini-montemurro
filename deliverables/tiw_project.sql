-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: tiw_project
-- ------------------------------------------------------
-- Server version	8.0.19

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
-- Table structure for table `appeal`
--

DROP TABLE IF EXISTS `appeal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appeal` (
  `id_appeal` int NOT NULL AUTO_INCREMENT,
  `id_course` int NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`id_appeal`),
  KEY `id_course_idx` (`id_course`),
  CONSTRAINT `id_course` FOREIGN KEY (`id_course`) REFERENCES `course` (`id_course`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appeal`
--

LOCK TABLES `appeal` WRITE;
/*!40000 ALTER TABLE `appeal` DISABLE KEYS */;
INSERT INTO `appeal` VALUES (1,1,'2021-06-19'),(2,1,'2021-02-13'),(3,1,'2021-06-12'),(4,2,'2021-06-19'),(5,3,'2021-01-11'),(6,3,'2021-02-11'),(7,4,'2021-04-20'),(8,4,'2021-05-28'),(9,5,'2021-05-23'),(10,5,'2021-02-24'),(11,6,'2021-01-12'),(12,6,'2021-04-12'),(13,7,'2021-06-24'),(14,7,'2021-03-12'),(15,7,'2021-02-25'),(16,8,'2020-08-28'),(17,8,'2020-09-28'),(18,9,'2021-02-02'),(19,9,'2021-02-16'),(20,10,'2021-03-03'),(21,10,'2021-02-20'),(22,10,'2021-05-20'),(23,11,'2021-03-11'),(24,11,'2020-09-29'),(25,12,'2021-06-02'),(26,12,'2021-06-12'),(27,12,'2021-06-24'),(28,13,'2020-01-22'),(29,13,'2021-01-01'),(30,14,'2021-02-21'),(31,15,'2020-12-21');
/*!40000 ALTER TABLE `appeal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id_course` int NOT NULL AUTO_INCREMENT,
  `id_professor` int NOT NULL,
  `title` varchar(45) NOT NULL,
  PRIMARY KEY (`id_course`),
  KEY `id_professor_idx` (`id_professor`),
  CONSTRAINT `id_professor` FOREIGN KEY (`id_professor`) REFERENCES `professor` (`id_professor`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,3,'The Divine Comedy'),(2,2,'Dubliners'),(3,3,'De Monarchia'),(4,3,'Convivio'),(5,1,'The Raven'),(6,2,'Ulysses'),(7,2,'A Portrait of the Artist as a Young Man'),(8,2,'Finnegans Wake'),(9,4,'Elegy Written in a Country Churchyard'),(10,4,'The Bard'),(11,5,'The Flowers of Evil'),(12,4,'The Fatal Sisters'),(13,6,'Romeo and Juliet'),(14,6,'Hamlet'),(15,1,'Annabel Lee');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam`
--

DROP TABLE IF EXISTS `exam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam` (
  `id_appeal` int NOT NULL,
  `id_student` int NOT NULL,
  `state` varchar(20) DEFAULT 'not entered',
  `failed` tinyint(1) DEFAULT NULL,
  `recalled` tinyint(1) DEFAULT NULL,
  `absent` tinyint(1) DEFAULT NULL,
  `grade` int DEFAULT NULL,
  `merit` tinyint(1) DEFAULT NULL,
  `id_report` int DEFAULT NULL,
  PRIMARY KEY (`id_appeal`,`id_student`),
  KEY `student_idx` (`id_student`),
  KEY `report_idx` (`id_report`),
  CONSTRAINT `appeal` FOREIGN KEY (`id_appeal`) REFERENCES `appeal` (`id_appeal`) ON UPDATE CASCADE,
  CONSTRAINT `student` FOREIGN KEY (`id_student`) REFERENCES `student` (`id_student`) ON UPDATE CASCADE,
  CONSTRAINT `enteredChecked` CHECK (((`state` <> _utf8mb4'entered') or ((`state` = _utf8mb4'entered') and (`failed` is not null) and (`recalled` is not null) and (`absent` is not null)))),
  CONSTRAINT `exam_chk_1` CHECK (((`merit` <> 1) or ((`grade` >= 30) and (`grade` <> NULL)))),
  CONSTRAINT `FailBooleans` CHECK ((((`failed` <> 1) or (`grade` < 18)) and ((`failed` <> 1) or (`absent` <> 1)) and ((`failed` <> 1) or (`recalled` <> 1)) and ((`absent` <> 1) or (`recalled` <> 1)) and ((`absent` <> 1) or (`grade` < 18)) and ((`recalled` <> 1) or (`grade` < 18)))),
  CONSTRAINT `GradeValues` CHECK (((`grade` >= 18) and (`grade` <= 30))),
  CONSTRAINT `MeritAvailability` CHECK ((((`merit` <> _utf8mb4'1') or (`grade` is not null)) and ((`merit` <> _utf8mb4'1') or (`grade` >= 30)))),
  CONSTRAINT `NullGrade` CHECK ((((`merit` is null) and (`grade` is null)) or ((`merit` is not null) and (`grade` is not null)))),
  CONSTRAINT `Reported` CHECK ((((`state` = _utf8mb4'recorded') and (`id_report` is not null)) or ((`id_report` is null) and (`state` <> _utf8mb4'recorded')))),
  CONSTRAINT `stateValues` CHECK ((`state` in (_utf8mb4'not entered',_utf8mb4'entered',_utf8mb4'published',_utf8mb4'refused',_utf8mb4'recorded')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam`
--

LOCK TABLES `exam` WRITE;
/*!40000 ALTER TABLE `exam` DISABLE KEYS */;
INSERT INTO `exam` VALUES (1,10000001,'entered',0,0,0,29,0,NULL),(1,10000002,'entered',0,0,1,NULL,NULL,NULL),(1,10000006,'entered',0,0,0,18,0,NULL),(1,10000010,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(2,10000002,'refused',1,0,0,NULL,NULL,NULL),(2,10000003,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(2,10000004,'published',1,0,0,NULL,NULL,NULL),(3,10000002,'published',0,0,1,NULL,NULL,NULL),(3,10000004,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(3,10000009,'entered',0,0,0,18,0,NULL),(4,10000002,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(4,10000004,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(4,10000005,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(4,10000007,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(4,10000008,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(5,10000001,'recorded',0,0,1,NULL,NULL,3),(5,10000005,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(5,10000009,'entered',0,0,0,23,0,NULL),(6,10000001,'entered',0,0,0,18,0,NULL),(6,10000010,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(7,10000008,'published',1,0,0,NULL,NULL,NULL),(8,10000001,'entered',0,0,0,20,0,NULL),(8,10000002,'entered',0,0,0,21,0,NULL),(8,10000008,'entered',0,0,0,22,0,NULL),(9,10000001,'entered',0,0,0,18,0,NULL),(9,10000002,'entered',0,0,0,29,0,NULL),(9,10000005,'entered',0,0,0,18,0,NULL),(9,10000007,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(10,10000002,'published',0,0,1,NULL,NULL,NULL),(10,10000004,'entered',0,0,0,26,0,NULL),(10,10000007,'refused',1,0,0,NULL,NULL,NULL),(11,10000007,'entered',0,0,0,20,0,NULL),(12,10000006,'entered',0,0,0,23,0,NULL),(13,10000002,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(13,10000004,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(13,10000006,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(14,10000002,'refused',1,0,0,NULL,NULL,NULL),(14,10000004,'refused',1,0,0,NULL,NULL,NULL),(14,10000005,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(15,10000002,'published',0,0,1,NULL,NULL,NULL),(15,10000003,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(15,10000006,'published',0,1,0,NULL,NULL,NULL),(16,10000004,'published',1,0,0,NULL,NULL,NULL),(16,10000005,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(17,10000004,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(18,10000009,'refused',1,0,0,NULL,NULL,NULL),(18,10000010,'published',0,0,1,NULL,NULL,NULL),(19,10000009,'entered',0,0,0,22,0,NULL),(19,10000010,'entered',0,0,0,30,1,NULL),(20,10000001,'refused',1,0,0,NULL,NULL,NULL),(20,10000004,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(20,10000005,'entered',0,0,0,20,0,NULL),(21,10000002,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(21,10000003,'published',0,0,1,NULL,NULL,NULL),(22,10000001,'published',0,0,0,24,0,NULL),(22,10000003,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(23,10000001,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(24,10000001,'published',0,0,1,NULL,NULL,NULL),(24,10000002,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(24,10000004,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(25,10000002,'published',0,0,1,NULL,NULL,NULL),(25,10000005,'published',0,0,1,NULL,NULL,NULL),(26,10000002,'recorded',0,0,1,NULL,NULL,1),(26,10000005,'recorded',1,0,0,NULL,NULL,2),(27,10000002,'entered',0,1,0,NULL,NULL,NULL),(27,10000005,'entered',0,0,0,25,0,NULL),(28,10000001,'recorded',0,0,0,23,0,6),(28,10000007,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(28,10000009,'entered',0,0,0,28,0,NULL),(29,10000003,'entered',0,0,0,21,0,NULL),(29,10000005,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(30,10000002,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(30,10000004,'entered',0,0,0,30,1,NULL),(30,10000006,'entered',0,0,0,25,0,NULL),(30,10000008,'not entered',NULL,NULL,NULL,NULL,NULL,NULL),(30,10000010,'entered',0,0,0,28,0,NULL),(31,10000001,'recorded',0,0,0,20,0,4),(31,10000003,'recorded',0,0,0,19,0,4),(31,10000005,'recorded',0,0,0,30,1,5),(31,10000010,'not entered',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `exam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `followings`
--

DROP TABLE IF EXISTS `followings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `followings` (
  `id_student` int NOT NULL,
  `id_course` int NOT NULL,
  PRIMARY KEY (`id_student`,`id_course`),
  KEY `course_idx` (`id_course`),
  CONSTRAINT `course_followed` FOREIGN KEY (`id_course`) REFERENCES `course` (`id_course`) ON UPDATE CASCADE,
  CONSTRAINT `student_courses` FOREIGN KEY (`id_student`) REFERENCES `student` (`id_student`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `followings`
--

LOCK TABLES `followings` WRITE;
/*!40000 ALTER TABLE `followings` DISABLE KEYS */;
INSERT INTO `followings` VALUES (10000001,1),(10000002,1),(10000003,1),(10000004,1),(10000006,1),(10000009,1),(10000010,1),(10627677,1),(10000002,2),(10000004,2),(10000005,2),(10000007,2),(10000008,2),(10000001,3),(10000005,3),(10000009,3),(10000010,3),(10000001,4),(10000002,4),(10000008,4),(10000001,5),(10000002,5),(10000004,5),(10000005,5),(10000007,5),(10000006,6),(10000007,6),(10000002,7),(10000003,7),(10000004,7),(10000005,7),(10000006,7),(10000004,8),(10000005,8),(10000009,9),(10000010,9),(10000001,10),(10000002,10),(10000003,10),(10000004,10),(10000005,10),(10000001,11),(10000002,11),(10000004,11),(10000002,12),(10000005,12),(10000001,13),(10000003,13),(10000005,13),(10000007,13),(10000009,13),(10000002,14),(10000004,14),(10000006,14),(10000008,14),(10000010,14),(10000001,15),(10000003,15),(10000005,15),(10000010,15);
/*!40000 ALTER TABLE `followings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `professor`
--

DROP TABLE IF EXISTS `professor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `professor` (
  `id_professor` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`id_professor`)
) ENGINE=InnoDB AUTO_INCREMENT=10000010 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `professor`
--

LOCK TABLES `professor` WRITE;
/*!40000 ALTER TABLE `professor` DISABLE KEYS */;
INSERT INTO `professor` VALUES (1,'Edgar Allan','Poe','edgarallan.poe@mail.provider.com','Boston'),(2,'James','Joyce','james.joyce@mail.provider.com','Dublino'),(3,'Dante','Alighieri','dante.alighieri@mail.provider.com','Firenze'),(4,'Thomas','Gray','thomas.gray@mail.provider.com','Londra'),(5,'Charles','Baudelaire','charles.baudelaire@mail.provider.com','Parigi'),(6,'William','Shakespeare','william.shakespeare@mail.provider.com','Warwick');
/*!40000 ALTER TABLE `professor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report` (
  `id_report` int NOT NULL AUTO_INCREMENT,
  `id_appeal` int NOT NULL,
  `date` date DEFAULT NULL,
  `hour` time DEFAULT NULL,
  PRIMARY KEY (`id_report`),
  KEY `appeal_idx` (`id_appeal`),
  KEY `appeal_rep_idx` (`id_appeal`),
  CONSTRAINT `appeal_rep` FOREIGN KEY (`id_appeal`) REFERENCES `appeal` (`id_appeal`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
INSERT INTO `report` VALUES (1,26,'2021-06-18','11:03:32'),(2,26,'2021-06-18','11:04:09'),(3,5,'2021-06-18','16:50:29'),(4,31,'2021-06-18','20:46:59'),(5,31,'2021-06-18','20:47:07'),(6,28,'2021-06-18','20:54:03');
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `security`
--

DROP TABLE IF EXISTS `security`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `security` (
  `id_person` int NOT NULL,
  `last_course` int DEFAULT NULL,
  `last_appeal` int DEFAULT NULL,
  `last_student` int DEFAULT NULL,
  PRIMARY KEY (`id_person`),
  KEY `last_course_idx` (`last_course`),
  KEY `last_appeal_idx` (`last_appeal`),
  KEY `last_student_idx` (`last_student`),
  CONSTRAINT `last_appeal` FOREIGN KEY (`last_appeal`) REFERENCES `appeal` (`id_appeal`) ON UPDATE CASCADE,
  CONSTRAINT `last_course` FOREIGN KEY (`last_course`) REFERENCES `course` (`id_course`) ON UPDATE CASCADE,
  CONSTRAINT `last_student` FOREIGN KEY (`last_student`) REFERENCES `student` (`id_student`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `security`
--

LOCK TABLES `security` WRITE;
/*!40000 ALTER TABLE `security` DISABLE KEYS */;
/*!40000 ALTER TABLE `security` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `id_student` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `degree_course` varchar(45) NOT NULL,
  PRIMARY KEY (`id_student`)
) ENGINE=InnoDB AUTO_INCREMENT=10669628 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (10000001,'Torquato','Tasso','torquato.tasso@mail.provider.com','Sorrento','Cinquecento'),(10000002,'Italo','Svevo','italo.svevo@mail.provider.com','Trieste','Novecento'),(10000003,'Giovanni','Verga','giovanni.verga@mail.provider.com','Catania','Ottocento'),(10000004,'Luigi','Pirandello','luigi.pirandello@mail.provider.com','Agrigento','Novecento'),(10000005,'Eugenio','Montale','eugenio.montale@mail.provider.com','Genova','Novecento'),(10000006,'Alessandro','Manzoni','alessandro.manzoni@mail.provider.com','Lecco','Ottocento'),(10000007,'Giuseppe','Parini','giuseppe.parini@mail.provider.com','Milano','Settecento'),(10000008,'Ludovico','Ariosto','ludovico.ariosto@mail.provider.com','ReggioEmilia','Cinquecento'),(10000009,'Ugo','Foscolo','ugo.foscolo@mail.provider.com','Zacinto','Ottocento'),(10000010,'Giacomo','Leopardi','giacomo.leopardi@mail.provider.com','Recanati','Ottocento');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-06-19  9:31:04
