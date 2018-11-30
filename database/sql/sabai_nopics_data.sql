-- MySQL dump 10.13  Distrib 5.7.24, for Linux (x86_64)
--
-- Host: localhost    Database: sabai
-- ------------------------------------------------------
-- Server version	5.7.24-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `consults`
--
CREATE DATABASE IF NOT EXISTS `sabai`;
USE `sabai`;

DROP TABLE IF EXISTS `consults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consults` (
  `consult_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `visit_id` int(11) unsigned NOT NULL,
  `date` varchar(100) NOT NULL,
  `doctor` varchar(255) NOT NULL,
  `notes` longtext NOT NULL,
  `diagnosis` longtext NOT NULL,
  `problems` varchar(255) NOT NULL,
  `urine_test` varchar(255) DEFAULT NULL,
  `hemocue_count` varchar(255) DEFAULT NULL,
  `blood_glucose` varchar(255) DEFAULT NULL,
  `referrals` varchar(255) DEFAULT NULL,
  `chronic_referral` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`consult_id`),
  KEY `visit_id` (`visit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1092 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fingerprint`
--

DROP TABLE IF EXISTS `fingerprint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fingerprint` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) unsigned NOT NULL,
  `fg_value` blob NOT NULL,
  `size` int(11) NOT NULL,
  `fg_image` mediumtext,
  PRIMARY KEY (`id`),
  KEY `patient_id` (`patient_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1551 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inventory` (
  `medicine_name` varchar(255) NOT NULL,
  `quantity` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`medicine_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orderlist`
--

DROP TABLE IF EXISTS `orderlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orderlist` (
  `order_id` int(10) unsigned NOT NULL,
  `medicine_name` varchar(255) NOT NULL,
  `quantity` int(10) NOT NULL DEFAULT '0',
  `notes` varchar(255) NOT NULL DEFAULT '0',
  `remarks` varchar(255) NOT NULL DEFAULT '-',
  KEY `orderlist_order_id_fk` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orders` (
  `order_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `visit_id` int(10) unsigned NOT NULL,
  `status` varchar(255) NOT NULL,
  PRIMARY KEY (`order_id`),
  KEY `orders_visit_id_fk` (`visit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=653 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patient_pictures`
--

DROP TABLE IF EXISTS `patient_pictures`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_pictures` (
  `patient_id` int(11) NOT NULL,
  `picture_blob` mediumblob,
  PRIMARY KEY (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patients`
--

DROP TABLE IF EXISTS `patients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patients` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `village_prefix` varchar(5) NOT NULL,
  `name` varchar(255) DEFAULT '',
  `image` varchar(255) DEFAULT '',
  `contactNo` varchar(255) DEFAULT NULL,
  `gender` varchar(6) DEFAULT '',
  `travelling_time_to_village` int(11) DEFAULT NULL,
  `date_of_birth` varchar(10) DEFAULT NULL,
  `drug_allergy` text,
  `parent` int(11) unsigned DEFAULT NULL,
  `face_encodings` varchar(3000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parent` (`parent`),
  CONSTRAINT `patients_ibfk_1` FOREIGN KEY (`parent`) REFERENCES `patients` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=780 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `postreferrals`
--

DROP TABLE IF EXISTS `postreferrals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `postreferrals` (
  `postreferral_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `visit_id` int(11) unsigned NOT NULL,
  `date` varchar(100) DEFAULT NULL,
  `recorder` varchar(255) NOT NULL DEFAULT '',
  `remarks` longtext,
  PRIMARY KEY (`postreferral_id`),
  KEY `visit_id` (`visit_id`),
  CONSTRAINT `visit_postreferral_fk` FOREIGN KEY (`visit_id`) REFERENCES `visits` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=125 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `name` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `account_type` varchar(255) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visits`
--

DROP TABLE IF EXISTS `visits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visits` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) unsigned NOT NULL,
  `date` varchar(100) NOT NULL,
  `status` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `fk_visit_patient_id` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=801 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vitals`
--

DROP TABLE IF EXISTS `vitals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vitals` (
  `vitals_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `visit_id` int(11) unsigned NOT NULL,
  `height` double unsigned DEFAULT NULL,
  `weight` double unsigned DEFAULT NULL,
  `systolic` double unsigned DEFAULT NULL,
  `diastolic` double unsigned DEFAULT NULL,
  `temperature` double DEFAULT NULL,
  `hiv_positive` int(11) DEFAULT NULL,
  `ptb_positive` int(11) DEFAULT NULL,
  `hepC_positive` int(11) NOT NULL,
  PRIMARY KEY (`vitals_id`),
  KEY `visit_id` (`visit_id`),
  CONSTRAINT `visit_vitals_fk` FOREIGN KEY (`visit_id`) REFERENCES `visits` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=831 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-11-30 10:30:22
