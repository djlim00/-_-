CREATE TABLE `User` (
    `user_id` bigint AUTO_INCREMENT PRIMARY KEY,
    `user_email` varchar(200) NOT NULL,
    `profile_image_url` text NULL,
    `introduction` text NULL,
    `nickname` varchar(200) NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status` varchar(255) NOT NULL DEFAULT 'active'
);