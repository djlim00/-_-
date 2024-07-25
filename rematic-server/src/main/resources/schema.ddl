-- 24/07/25
CREATE TABLE `Post` (
                        `post_id`	BIGINT	NOT NULL auto_increment primary key ,
                        `title`	TEXT	NOT NULL,
                        `content`	TEXT	NOT NULL,
                        `has_image`	varchar(200)	NOT NULL	DEFAULT '없음',
                        `category`	varchar(200)	NOT NULL,
                        `genre`	varchar(200)	NOT NULL,
                        `hates`	INT	NOT NULL	DEFAULT 0,
                        `likes`	INT	NOT NULL	DEFAULT 0,
                        `scraps`	INT	NOT NULL	DEFAULT 0,
                        `views`	INT	NOT NULL	DEFAULT 0,
                        `realtime_views`	int	NOT NULL	DEFAULT 0,
                        `previous_realtime_views` int	NOT NULL	DEFAULT 0,
                        `anonymity`	varchar(200)	NOT NULL	DEFAULT '공개',
                        `status`	varchar(200)	NOT NULL	DEFAULT 'active',
                        `created_at`	timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                        `updated_at`	timestamp	NOT NULL	DEFAULT current_timestamp,
                        `user_id`	bigint	NOT NULL,
                        `bulletin_id`	bigint	NOT NULL
);

CREATE TABLE `User` (
                        `user_id`	bigint	NOT NULL auto_increment primary key ,
                        `profile_image_url`	TEXT	NULL,
                        `introduction`	TEXT	NULL,
                        `nickname`	varchar(200)	NOT NULL,
                        `user_email`	varchar(200)	NOT NULL,
                        `status`	varchar(200)	NOT NULL	DEFAULT 'active',
                        `created_at`	timestamp	NOT NULL	DEFAULT current_timestamp,
                        `bulletin_id`	bigint	NULL
);

CREATE TABLE `Comment` (
                           `comment_id`	bigint	NOT NULL auto_increment primary key ,
                           `sentences`	text	NOT NULL,
                           `likes`	bigint	NOT NULL	DEFAULT 0,
                           `hates`	bigint	NOT NULL	DEFAULT 0,
                           `comment_image_url`	text	NULL,
                           `parent_id`	bigint	NOT NULL,
                           `alarm_status`	varchar(200)	NOT NULL	DEFAULT 'on',
                           `status`	varchar(200)	NOT NULL	DEFAULT 'active',
                           `created_at`	timestamp	NOT NULL	DEFAULT current_timestamp,
                           `post_id`	bigint	NOT NULL,
                           `user_id`	bigint	NOT NULL
);

CREATE TABLE `Recent_Keyword` (
                                  `recent_keyword_id`	bigint	NOT NULL auto_increment primary key ,
                                  `keyword`	text	NOT NULL,
                                  `created_at`	timestamp	NOT NULL	DEFAULT current_timestamp,
                                  `status`	varchar(200)	NOT NULL	DEFAULT 'active',
                                  `user_id`	bigint	NOT NULL
);

CREATE TABLE `Bulletin` (
                            `bulletin_id`	bigint	NOT NULL auto_increment primary key ,
                            `name`	varchar(200)	NOT NULL,
                            `genre`	varchar(200)	NOT NULL,
                            `origin_category`	varchar(200)	NOT NULL,
                            `category`	varchar(200)	NOT NULL,
                            `thumnail_image_url`	text	NULL,
                            `status`	varchar(200)	NOT NULL	DEFAULT 'active'
);

CREATE TABLE `Ranking` (
                           `ranked_post_id`	bigint	NOT NULL auto_increment primary key ,
                           `realtime_views`	bigint	NOT NULL,
                           `post_id`	BIGINT	NOT NULL
);

CREATE TABLE `PostImage` (
                             `post_image_id`	bigint	NOT NULL auto_increment primary key ,
                             `description`	text	NULL,
                             `image_url`	text	NOT NULL,
                             `order` bigint NOT NULL,
                             `status`	varchar(200)	NOT NULL	DEFAULT 'active'	COMMENT 'dormant',
                             `post_id`	BIGINT	NOT NULL
);

CREATE TABLE `UserScrap` (
                             `scrap_id`	BIGINT	NOT NULL auto_increment primary key ,
                             `created_at`	timestamp	NOT NULL	DEFAULT current_timestamp,
                             `user_id`	bigint	NOT NULL,
                             `post_id`	BIGINT	NOT NULL
);

CREATE TABLE `PostLikes` (
                             `post_likes_id`	bigint	NOT NULL auto_increment primary key ,
                             `created_at`	timestamp	NOT NULL	DEFAULT current_timestamp,
                             `user_id`	bigint	NOT NULL,
                             `post_id`	BIGINT	NOT NULL
);

CREATE TABLE `PostHates` (
                             `post_hates_id`	bigint	NOT NULL auto_increment primary key ,
                             `created_at`	timestamp	NOT NULL	DEFAULT current_timestamp,
                             `user_id`	bigint	NOT NULL,
                             `post_id`	BIGINT	NOT NULL
);

CREATE TABLE `CommentLikes` (
                                `comment_likes_id`	bigint	NOT NULL auto_increment primary key ,
                                `created_at`	timestamp	NOT NULL	DEFAULT current_timestamp,
                                `user_id`	bigint	NOT NULL,
                                `comment_id`	bigint	NOT NULL
);

CREATE TABLE `CommentHates` (
                                `comment_hates_id`	bigint	NOT NULL auto_increment primary key ,
                                `created_at`	timestamp	NOT NULL	DEFAULT current_timestamp,
                                `user_id`	bigint	NOT NULL,
                                `comment_id`	bigint	NOT NULL
);

ALTER TABLE User ADD foreign key(bulletin_id) references Bulletin(bulletin_id);

ALTER TABLE Recent_Keyword ADD foreign key(user_id) references User(user_id);

ALTER TABLE Post ADD foreign key(user_id) references User(user_id);
ALTER TABLE Post ADD foreign key(bulletin_id) references Bulletin(bulletin_id);

ALTER TABLE PostImage ADD foreign key(post_id) references Post(post_id);

ALTER TABLE UserScrap ADD foreign key (user_id) references User(user_id);
ALTER TABLE UserScrap ADD foreign key (post_id) references Post(post_id);

ALTER TABLE PostLikes ADD foreign key (user_id) references User(user_id);
ALTER TABLE PostLikes ADD foreign key (post_id) references Post(post_id);

ALTER TABLE PostHates ADD foreign key (user_id) references User(user_id);
ALTER TABLE PostHates ADD foreign key (post_id) references Post(post_id);

ALTER TABLE Comment ADD foreign key(post_id) references Post(post_id);
ALTER TABLE Comment ADD foreign key (user_id) references User(user_id);

ALTER TABLE CommentLikes ADD foreign key (user_id) references User(user_id);
ALTER TABLE CommentLikes ADD foreign key (user_id) references Comment(comment_id);

ALTER TABLE CommentHates ADD foreign key (user_id) references User(user_id);
ALTER TABLE CommentHates ADD foreign key (user_id) references Comment(comment_id);

-- 24/07/12 week2 ver.
-- CREATE TABLE `User` (
--                         `user_id`	bigint	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
--                         `profile_image_url`	TEXT	NULL,
--                         `introduction`	TEXT	NULL,
--                         `nickname`	varchar(200)	NOT NULL,
--                         `user_email`	varchar(200)	NOT NULL,
--                         `status`	varchar(200)	NOT NULL	DEFAULT 'active',
--                         `created_at` timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
--                         `updated_at` timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--                         `bulletin_id`bigint NULL
-- );
--
-- CREATE TABLE `Post` (
--                         `post_id`	BIGINT(20)	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
--                         `title`	TEXT	NOT NULL,
--                         `content`	TEXT	NOT NULL,
--                         `writer_nickname`	varchar(200)	NOT NULL,
--                         `images`	TEXT	NULL,
--                         `category`	varchar(200)	NOT NULL,
--                         `hates`	INT	NOT NULL	DEFAULT 0,
--                         `likes`	INT	NOT NULL	DEFAULT 0,
--                         `scraps`	INT	NOT NULL	DEFAULT 0,
--                         `views`	INT	NOT NULL	DEFAULT 0,
--                         `realtime_views`	bigint	NOT NULL DEFAULT 0,
--                         `anonymity`	BOOLEAN	NOT NULL,
--                         `status`	varchar(200)	NULL	DEFAULT 'active',
--                         `created_at` timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
--                         `updated_at` timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--                         `user_id`	bigint	NOT NULL,
--                         `bulletin_id`	bigint	NOT NULL
-- );
--
-- CREATE TABLE `Comment` (
--                            `comment_id`	bigint	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
--                            `likes`	bigint	NOT NULL	DEFAULT 0,
--                            `hates`	bigint	NOT NULL	DEFAULT 0,
--                            `created_at`	timestamp	NOT NULL	DEFAULT current_timestamp,
--                            `updated_at` timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--                            `parent_id`	bigint	NULL,
--                            `post_id`	BIGINT	NOT NULL,
--                            `user_id`	bigint	NOT NULL
-- );
--
-- CREATE TABLE `Recent_Keyword` (
--                                   `recent_keyword_id`	bigint	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
--                                   `keyword`	text	NOT NULL,
--                                   `created_at`	timestamp	NOT NULL DEFAULT current_timestamp,
--                                   `status`	varchar(200)	NOT NULL DEFAULT 'active',
--                                   `user_id`	bigint	NOT NULL
-- );
--
-- ALTER TABLE Post ADD foreign key(user_id) references User(user_id);
-- ALTER TABLE Comment ADD foreign key(post_id) references Post(post_id);
-- ALTER TABLE Comment ADD foreign key (user_id) references User(user_id);
-- ALTER TABLE Comment ADD foreign key (parent_id) references Comment(comment_id);
-- ALTER TABLE Recent_Keyword ADD foreign key(user_id) references User(user_id);
--
-- CREATE TABLE `Bulletin` (
--                             `bulletin_id`	bigint	NOT NULL auto_increment primary key ,
--                             `name`	varchar(200)	NOT NULL,
--                             `genre`	varchar(200)	NOT NULL,
--                             `category`	varchar(200)	NOT NULL,
--                             `thumnail_image_url`	text	NULL,
--                             `preview_video_url`	text	NULL
-- );
--
-- CREATE TABLE `Ranking` (
--                            `ranked_post_id`	bigint	NOT NULL auto_increment primary key,
--                            `realtime_views`	bigint	NOT NULL,
--                            `post_id`	BIGINT	NOT NULL
-- );
--
-- ALTER TABLE Post ADD foreign key(bulletin_id) references Bulletin(bulletin_id);
-- ALTER TABLE Ranking ADD foreign key(post_id) references Post(post_id);

-- CREATE TABLE `user` (
--                         `user_id`	bigint	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
--                         `profile_image_url`	TEXT	NULL,
--                         `introduction`	TEXT	NULL,
--                         `nickname`	varchar(200)	NOT NULL,
--                         `user_email`	varchar(200)	NOT NULL,
--                         `status`	varchar(200)	NOT NULL	DEFAULT 'active',
--                         `created_at` timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
--                         `updated_at` timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );
--
-- CREATE TABLE `post` (
--                         `post_id`	BIGINT(20)	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
--                         `title`	TEXT	NOT NULL,
--                         `content`	TEXT	NOT NULL,
--                         `writer_nickname`	varchar(200)	NOT NULL,
--                         `images`	TEXT	NULL,
--                         `category`	varchar(200)	NOT NULL,
--                         `hates`	INT	NOT NULL	DEFAULT 0,
--                         `likes`	INT	NOT NULL	DEFAULT 0,
--                         `scrap`	INT	NOT NULL	DEFAULT 0,
--                         `views`	INT	NOT NULL	DEFAULT 0,
--                         `realtime_views`	VARCHAR(255)	NOT NULL,
--                         `anonymity`	BOOLEAN	NOT NULL,
--                         `status`	varchar(200)	NULL	DEFAULT 'active',
--                         `created_at` timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
--                         `updated_at` timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--                         `user_id`	bigint	NOT NULL
--
-- );
--
-- CREATE TABLE `comment` (
--                            `comment_id`	bigint	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
--                            `likes`	bigint	NOT NULL	DEFAULT 0,
--                            `hates`	bigint	NOT NULL	DEFAULT 0,
--                            `created_at`	timestamp	NOT NULL	DEFAULT current_timestamp,
--                            `updated_at` timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--                            `parent_id`	bigint	NULL,
--                            `post_id`	BIGINT	NOT NULL,
--                            `user_id`	bigint	NOT NULL
-- );
--
-- CREATE TABLE `recent_keyword` (
--                                   `recent_keyword_id`	bigint	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
--                                   `keyword`	text	NOT NULL,
--                                   `created_at`	timestamp	NOT NULL DEFAULT current_timestamp,
--                                   `status`	varchar(200)	NOT NULL DEFAULT 'active',
--                                   `user_id`	bigint	NOT NULL
-- );
--
-- ALTER TABLE post ADD foreign key(user_id) references user(user_id);
-- ALTER TABLE comment ADD foreign key(post_id) references post(post_id);
-- ALTER TABLE comment ADD foreign key (user_id) references user(user_id);
-- ALTER TABLE comment ADD foreign key (parent_id) references comment(comment_id);
-- ALTER TABLE recent_keyword ADD foreign key(user_id) references user(user_id);

-- CREATE TABLE `User` (
--     `user_id` bigint AUTO_INCREMENT PRIMARY KEY,
--     `user_email` varchar(200) NOT NULL,
--     `profile_image_url` text NULL,
--     `introduction` text NULL,
--     `nickname` varchar(200) NOT NULL,
--     `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     `status` varchar(255) NOT NULL DEFAULT 'active'
-- );