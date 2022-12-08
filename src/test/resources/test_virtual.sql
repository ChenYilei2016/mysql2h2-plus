CREATE TABLE `sales` (
                         `id` int(11) NOT NULL,
                         `province` varchar(12) GENERATED ALWAYS AS (substr(`confirmation_number`,9,2)) VIRTUAL,
                         `customer_id` int(11) NOT NULL,
                         `confirmation_number` varchar(12) NOT NULL,
                         `order_id` int(11) DEFAULT NULL,
                         `order_type` varchar(10) DEFAULT NULL,
                         `start_date` date NOT NULL,
                         `end_date` date DEFAULT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

