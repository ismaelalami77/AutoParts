-- MySQL dump 10.13  Distrib 8.0.43, for macos15 (arm64)
--
-- Host: localhost    Database: AutoPartsDP
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `AutoPartsDP`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `AutoPartsDP` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `AutoPartsDP`;

--
-- Table structure for table `Branch`
--

DROP TABLE IF EXISTS `Branch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Branch` (
  `branch_id` int NOT NULL AUTO_INCREMENT,
  `branch_name` varchar(100) NOT NULL,
  `city` varchar(100) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`branch_id`),
  UNIQUE KEY `uq_branch_phone` (`phone`),
  UNIQUE KEY `unique_branch_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Branch`
--

LOCK TABLES `Branch` WRITE;
/*!40000 ALTER TABLE `Branch` DISABLE KEYS */;
INSERT INTO `Branch` VALUES (1,'Ramallah Branch','Ramallah','Al Irsal Street','0599111111'),(2,'Nablus Branch','Nablus','Rafidia Street','0599111112'),(3,'Hebron Branch','Hebron','Ein Sarah Street','0599111113');
/*!40000 ALTER TABLE `Branch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Branch_Inventory`
--

DROP TABLE IF EXISTS `Branch_Inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Branch_Inventory` (
  `branch_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int DEFAULT NULL,
  `last_updated` date DEFAULT NULL,
  PRIMARY KEY (`branch_id`,`product_id`),
  KEY `fk_branch_inventory_product` (`product_id`),
  CONSTRAINT `fk_branch_inventory_branch` FOREIGN KEY (`branch_id`) REFERENCES `Branch` (`branch_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_branch_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `Product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Branch_Inventory`
--

LOCK TABLES `Branch_Inventory` WRITE;
/*!40000 ALTER TABLE `Branch_Inventory` DISABLE KEYS */;
INSERT INTO `Branch_Inventory` VALUES (1,1,12,'2026-06-20'),(1,2,8,'2026-06-20'),(1,3,25,'2026-06-20'),(1,4,4,'2026-06-20'),(1,5,6,'2026-06-20'),(1,6,5,'2026-06-20'),(1,7,20,'2026-06-20'),(1,8,2,'2026-06-20'),(1,9,3,'2026-06-20'),(1,10,4,'2026-06-20'),(1,11,4,'2026-06-20'),(1,12,1,'2026-06-20'),(1,13,5,'2026-06-20'),(1,14,18,'2026-06-20'),(1,15,3,'2026-06-20'),(1,16,5,'2026-06-20'),(1,17,10,'2026-06-20'),(1,18,25,'2026-06-20'),(2,1,15,'2026-06-20'),(2,2,12,'2026-06-20'),(2,3,18,'2026-06-20'),(2,4,6,'2026-06-20'),(2,5,9,'2026-06-20'),(2,6,8,'2026-06-20'),(2,7,14,'2026-06-20'),(2,8,1,'2026-06-20'),(2,9,2,'2026-06-20'),(2,10,3,'2026-06-20'),(2,11,3,'2026-06-20'),(2,12,2,'2026-06-20'),(2,13,4,'2026-06-20'),(2,14,22,'2026-06-20'),(2,15,4,'2026-06-20'),(2,16,7,'2026-06-20'),(2,17,12,'2026-06-20'),(2,18,20,'2026-06-20'),(3,1,9,'2026-06-20'),(3,2,7,'2026-06-20'),(3,3,30,'2026-06-20'),(3,4,3,'2026-06-20'),(3,5,5,'2026-06-20'),(3,6,4,'2026-06-20'),(3,7,10,'2026-06-20'),(3,8,1,'2026-06-20'),(3,9,1,'2026-06-20'),(3,10,2,'2026-06-20'),(3,11,2,'2026-06-20'),(3,12,1,'2026-06-20'),(3,13,3,'2026-06-20'),(3,14,16,'2026-06-20'),(3,15,2,'2026-06-20'),(3,16,4,'2026-06-20'),(3,17,8,'2026-06-20'),(3,18,18,'2026-06-20');
/*!40000 ALTER TABLE `Branch_Inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Category`
--

DROP TABLE IF EXISTS `Category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) NOT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Category`
--

LOCK TABLES `Category` WRITE;
/*!40000 ALTER TABLE `Category` DISABLE KEYS */;
INSERT INTO `Category` VALUES (1,'Engine Parts'),(2,'Electrical Parts'),(3,'Brake System'),(4,'Lighting'),(5,'Filters'),(6,'Oils and Fluids'),(7,'Body Parts'),(8,'Cooling System'),(9,'Suspension'),(10,'Interior Parts');
/*!40000 ALTER TABLE `Category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Customer`
--

DROP TABLE IF EXISTS `Customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Customer` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `unique_customer_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Customer`
--

LOCK TABLES `Customer` WRITE;
/*!40000 ALTER TABLE `Customer` DISABLE KEYS */;
INSERT INTO `Customer` VALUES (1,'Abood Mousa','0568996689','Al-Baloo'),(2,'Qays Khateeb','0592280140','AL-Ersal');
/*!40000 ALTER TABLE `Customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Employee`
--

DROP TABLE IF EXISTS `Employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Employee` (
  `employee_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `position` varchar(50) DEFAULT NULL,
  `salary` decimal(10,2) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `hire_date` date DEFAULT NULL,
  `branch_id` int DEFAULT NULL,
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `unique_employee_username` (`username`),
  UNIQUE KEY `unique_employee_phone` (`phone`),
  KEY `fk_employee_branch` (`branch_id`),
  CONSTRAINT `fk_employee_branch` FOREIGN KEY (`branch_id`) REFERENCES `Branch` (`branch_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Employee`
--

LOCK TABLES `Employee` WRITE;
/*!40000 ALTER TABLE `Employee` DISABLE KEYS */;
INSERT INTO `Employee` VALUES (1,'ismael','1234','Ismael Alami','Manager',4500.00,'0592342235','2020-06-20',1),(2,'ihab','1234','Ihab Fawaqa','Sales Employee',3000.00,'0528061129','2024-03-10',1),(3,'sara','1234','Sara Khaled','Cashier',2900.00,'0599555503','2024-04-15',2);
/*!40000 ALTER TABLE `Employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Manager`
--

DROP TABLE IF EXISTS `Manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Manager` (
  `branch_id` int NOT NULL,
  `employee_id` int NOT NULL,
  PRIMARY KEY (`branch_id`),
  KEY `fk_manager_employee` (`employee_id`),
  CONSTRAINT `fk_manager_branch` FOREIGN KEY (`branch_id`) REFERENCES `Branch` (`branch_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_manager_employee` FOREIGN KEY (`employee_id`) REFERENCES `Employee` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Manager`
--

LOCK TABLES `Manager` WRITE;
/*!40000 ALTER TABLE `Manager` DISABLE KEYS */;
INSERT INTO `Manager` VALUES (1,1);
/*!40000 ALTER TABLE `Manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Payment`
--

DROP TABLE IF EXISTS `Payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Payment` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `payment_date` date DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `sales_order_id` int DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `fk_payment_sales` (`sales_order_id`),
  CONSTRAINT `fk_payment_sales` FOREIGN KEY (`sales_order_id`) REFERENCES `Sales_Order` (`sales_order_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Payment`
--

LOCK TABLES `Payment` WRITE;
/*!40000 ALTER TABLE `Payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `Payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Product`
--

DROP TABLE IF EXISTS `Product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Product` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `product_name` varchar(100) NOT NULL,
  `brand` varchar(100) DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `reorder_level` int DEFAULT NULL,
  `category_id` int DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  KEY `fk_product_category` (`category_id`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `Category` (`category_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Product`
--

LOCK TABLES `Product` WRITE;
/*!40000 ALTER TABLE `Product` DISABLE KEYS */;
INSERT INTO `Product` VALUES (1,'Oil Filter','Bosch',35.00,10,5),(2,'Air Filter','Mann Filter',45.00,10,5),(3,'Spark Plug','NGK',25.00,20,2),(4,'Battery','Varta',420.00,5,2),(5,'Brake Pads Front','Brembo',120.00,8,3),(6,'Brake Disc Front','Brembo',180.00,6,3),(7,'Brake Fluid DOT4','Bosch',40.00,15,6),(8,'Headlight Left','Mercedes',650.00,3,4),(9,'Headlight Right','Mercedes',650.00,3,4),(10,'Tail Light Left','Mercedes',550.00,3,4),(11,'Tail Light Right','Mercedes',550.00,3,4),(12,'Front Bumper','Mercedes',900.00,2,7),(13,'Side Mirror Left','Mercedes',350.00,4,7),(14,'Engine Oil 5W-30','Castrol',95.00,15,6),(15,'Radiator','Denso',480.00,4,8),(16,'Shock Absorber Front','Monroe',260.00,6,9),(17,'Seat Cover Set','AutoStyle',150.00,8,10),(18,'Wiper Blades','Bosch',65.00,20,10);
/*!40000 ALTER TABLE `Product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Purchase_Order`
--

DROP TABLE IF EXISTS `Purchase_Order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Purchase_Order` (
  `purchase_order_id` int NOT NULL AUTO_INCREMENT,
  `order_date` date DEFAULT NULL,
  `total_amount` decimal(10,2) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `supplier_id` int DEFAULT NULL,
  `warehouse_id` int DEFAULT NULL,
  PRIMARY KEY (`purchase_order_id`),
  KEY `fk_purchase_supplier` (`supplier_id`),
  KEY `fk_purchase_warehouse` (`warehouse_id`),
  CONSTRAINT `fk_purchase_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `Supplier` (`supplier_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_purchase_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `Warehouse` (`warehouse_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Purchase_Order`
--

LOCK TABLES `Purchase_Order` WRITE;
/*!40000 ALTER TABLE `Purchase_Order` DISABLE KEYS */;
/*!40000 ALTER TABLE `Purchase_Order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Purchase_Order_Item`
--

DROP TABLE IF EXISTS `Purchase_Order_Item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Purchase_Order_Item` (
  `purchase_order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int DEFAULT NULL,
  `unit_cost` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`purchase_order_id`,`product_id`),
  KEY `fk_poi_product` (`product_id`),
  CONSTRAINT `fk_poi_order` FOREIGN KEY (`purchase_order_id`) REFERENCES `Purchase_Order` (`purchase_order_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_poi_product` FOREIGN KEY (`product_id`) REFERENCES `Product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Purchase_Order_Item`
--

LOCK TABLES `Purchase_Order_Item` WRITE;
/*!40000 ALTER TABLE `Purchase_Order_Item` DISABLE KEYS */;
/*!40000 ALTER TABLE `Purchase_Order_Item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Sales_Order`
--

DROP TABLE IF EXISTS `Sales_Order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Sales_Order` (
  `sales_order_id` int NOT NULL AUTO_INCREMENT,
  `order_date` date DEFAULT NULL,
  `total_amount` decimal(10,2) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `customer_id` int DEFAULT NULL,
  `employee_id` int DEFAULT NULL,
  `branch_id` int DEFAULT NULL,
  PRIMARY KEY (`sales_order_id`),
  KEY `fk_sales_customer` (`customer_id`),
  KEY `fk_sales_branch` (`branch_id`),
  KEY `fk_sales_employee` (`employee_id`),
  CONSTRAINT `fk_sales_branch` FOREIGN KEY (`branch_id`) REFERENCES `Branch` (`branch_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_sales_customer` FOREIGN KEY (`customer_id`) REFERENCES `Customer` (`customer_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_sales_employee` FOREIGN KEY (`employee_id`) REFERENCES `Employee` (`employee_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Sales_Order`
--

LOCK TABLES `Sales_Order` WRITE;
/*!40000 ALTER TABLE `Sales_Order` DISABLE KEYS */;
/*!40000 ALTER TABLE `Sales_Order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Sales_Order_Item`
--

DROP TABLE IF EXISTS `Sales_Order_Item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Sales_Order_Item` (
  `sales_order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`sales_order_id`,`product_id`),
  KEY `fk_soi_product` (`product_id`),
  CONSTRAINT `fk_soi_product` FOREIGN KEY (`product_id`) REFERENCES `Product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_soi_sales` FOREIGN KEY (`sales_order_id`) REFERENCES `Sales_Order` (`sales_order_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Sales_Order_Item`
--

LOCK TABLES `Sales_Order_Item` WRITE;
/*!40000 ALTER TABLE `Sales_Order_Item` DISABLE KEYS */;
/*!40000 ALTER TABLE `Sales_Order_Item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Supplier`
--

DROP TABLE IF EXISTS `Supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Supplier` (
  `supplier_id` int NOT NULL AUTO_INCREMENT,
  `supplier_name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`supplier_id`),
  UNIQUE KEY `unique_supplier_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Supplier`
--

LOCK TABLES `Supplier` WRITE;
/*!40000 ALTER TABLE `Supplier` DISABLE KEYS */;
INSERT INTO `Supplier` VALUES (1,'German Auto Parts Co','0599333331','germanparts@gmail.com','Ramallah - Industrial Area'),(2,'Bosch Palestine Supply','0599333332','boschps@gmail.com','Nablus - Rafidia'),(3,'Mercedes Spare Parts Center','0599333333','mercedesparts@gmail.com','Hebron - Ein Sarah'),(4,'Premium Auto Lights','0599333334','autolights@gmail.com','Bethlehem - Main Street'),(5,'Quick Brake Supply','0599333335','brakes@gmail.com','Jenin - Main Street'),(6,'Cooling System Supply','0599333336','cooling@gmail.com','Ramallah - Beitunia');
/*!40000 ALTER TABLE `Supplier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Supplier_Product`
--

DROP TABLE IF EXISTS `Supplier_Product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Supplier_Product` (
  `supplier_id` int NOT NULL,
  `product_id` int NOT NULL,
  `supply_price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`supplier_id`,`product_id`),
  KEY `fk_supplier_product_product` (`product_id`),
  CONSTRAINT `fk_supplier_product_product` FOREIGN KEY (`product_id`) REFERENCES `Product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_supplier_product_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `Supplier` (`supplier_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Supplier_Product`
--

LOCK TABLES `Supplier_Product` WRITE;
/*!40000 ALTER TABLE `Supplier_Product` DISABLE KEYS */;
INSERT INTO `Supplier_Product` VALUES (1,3,15.00),(1,4,330.00),(1,12,750.00),(1,13,270.00),(2,1,24.00),(2,2,30.00),(2,7,28.00),(2,14,70.00),(2,18,45.00),(3,8,520.00),(3,9,520.00),(3,10,430.00),(3,11,430.00),(3,12,760.00),(4,8,500.00),(4,9,500.00),(4,10,410.00),(4,11,410.00),(5,5,90.00),(5,6,135.00),(5,7,28.00),(6,15,390.00),(6,16,200.00);
/*!40000 ALTER TABLE `Supplier_Product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Transfer`
--

DROP TABLE IF EXISTS `Transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Transfer` (
  `transfer_id` int NOT NULL AUTO_INCREMENT,
  `transfer_date` date DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `warehouse_id` int DEFAULT NULL,
  `branch_id` int DEFAULT NULL,
  PRIMARY KEY (`transfer_id`),
  KEY `fk_transfer_warehouse` (`warehouse_id`),
  KEY `fk_transfer_branch` (`branch_id`),
  CONSTRAINT `fk_transfer_branch` FOREIGN KEY (`branch_id`) REFERENCES `Branch` (`branch_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_transfer_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `Warehouse` (`warehouse_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Transfer`
--

LOCK TABLES `Transfer` WRITE;
/*!40000 ALTER TABLE `Transfer` DISABLE KEYS */;
/*!40000 ALTER TABLE `Transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Transfer_Item`
--

DROP TABLE IF EXISTS `Transfer_Item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Transfer_Item` (
  `transfer_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int DEFAULT NULL,
  PRIMARY KEY (`transfer_id`,`product_id`),
  KEY `fk_transfer_item_product` (`product_id`),
  CONSTRAINT `fk_transfer_item_product` FOREIGN KEY (`product_id`) REFERENCES `Product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_transfer_item_transfer` FOREIGN KEY (`transfer_id`) REFERENCES `Transfer` (`transfer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Transfer_Item`
--

LOCK TABLES `Transfer_Item` WRITE;
/*!40000 ALTER TABLE `Transfer_Item` DISABLE KEYS */;
/*!40000 ALTER TABLE `Transfer_Item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Warehouse`
--

DROP TABLE IF EXISTS `Warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Warehouse` (
  `warehouse_id` int NOT NULL AUTO_INCREMENT,
  `warehouse_name` varchar(100) NOT NULL,
  `city` varchar(100) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`warehouse_id`),
  UNIQUE KEY `unique_warehouse_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Warehouse`
--

LOCK TABLES `Warehouse` WRITE;
/*!40000 ALTER TABLE `Warehouse` DISABLE KEYS */;
INSERT INTO `Warehouse` VALUES (1,'Main Warehouse','Ramallah','Beitunia Industrial Area','0599222221'),(2,'North Warehouse','Nablus','Beit Iba Road','0599222222'),(3,'South Warehouse','Hebron','Industrial Area','0599222223');
/*!40000 ALTER TABLE `Warehouse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Warehouse_Inventory`
--

DROP TABLE IF EXISTS `Warehouse_Inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Warehouse_Inventory` (
  `warehouse_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int DEFAULT NULL,
  `last_updated` date DEFAULT NULL,
  PRIMARY KEY (`warehouse_id`,`product_id`),
  KEY `fk_warehouse_inventory_product` (`product_id`),
  CONSTRAINT `fk_warehouse_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `Product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_warehouse_inventory_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `Warehouse` (`warehouse_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Warehouse_Inventory`
--

LOCK TABLES `Warehouse_Inventory` WRITE;
/*!40000 ALTER TABLE `Warehouse_Inventory` DISABLE KEYS */;
INSERT INTO `Warehouse_Inventory` VALUES (1,1,100,'2026-06-20'),(1,2,90,'2026-06-20'),(1,3,150,'2026-06-20'),(1,4,25,'2026-06-20'),(1,5,60,'2026-06-20'),(1,6,40,'2026-06-20'),(2,7,100,'2026-06-20'),(2,8,20,'2026-06-20'),(2,9,20,'2026-06-20'),(2,10,25,'2026-06-20'),(2,11,25,'2026-06-20'),(2,14,80,'2026-06-20'),(3,12,10,'2026-06-20'),(3,13,25,'2026-06-20'),(3,15,15,'2026-06-20'),(3,16,30,'2026-06-20'),(3,17,50,'2026-06-20'),(3,18,120,'2026-06-20');
/*!40000 ALTER TABLE `Warehouse_Inventory` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-20 21:18:11
