

CREATE SCHEMA IF NOT EXISTS `test_db` ;
USE `test_db` ;

DROP TABLE IF EXISTS `User_T` ;
CREATE  TABLE User_T (
  username VARCHAR(45) NOT NULL,
  password VARCHAR(45) NOT NULL,
  role_id TINYINT NOT NULL DEFAULT 3 COMMENT '1=SYS_ADMIN, 2=ADMIN, 3=USER',
  active TINYINT NOT NULL DEFAULT 0 COMMENT '0=inactive, 1=active, 2=inactive by reson 1, 3=inactive by reason 2',
  PRIMARY KEY (username))
ENGINE = InnoDB;
  
DROP TABLE IF EXISTS `Permission_T` ;
CREATE  TABLE Permission_T (
  id BIGINT NOT NULL,
  permission VARCHAR(45) NOT NULL ,
  PRIMARY KEY (id))
ENGINE = InnoDB;

DROP TABLE IF EXISTS `Role_T` ;
CREATE  TABLE Role_T (
  id TINYINT NOT NULL AUTO_INCREMENT,
  role VARCHAR(45) NOT NULL ,
  PRIMARY KEY (id))
ENGINE = InnoDB;

DROP TABLE IF EXISTS `UserEmail_T` ;
CREATE  TABLE UserEmail_T (
  user_id BIGINT NOT NULL,
  email VARCHAR(45) NOT NULL,
  active BIT(3) NULL DEFAULT 0 COMMENT '0=inactive, 1=active, 2=inactive by reson 1, 3=inactive by reason 2',
  verified BIT(1) NULL DEFAULT 0 COMMENT '0=unverified, 1=verified',
  is_primary BIT(1) NULL DEFAULT 0 COMMENT '0=secondary, 1=primary',
  PRIMARY KEY (user_id, email))
ENGINE = InnoDB;

DROP TABLE IF EXISTS `InviteToRegister_T` ;
CREATE  TABLE UserInvite_T (
  invited_by BIGINT NOT NULL,
  recipient_email VARCHAR(45) NOT NULL,
  date_invited DATETIME NOT NULL,
  status BIT(3) NULL DEFAULT 0 COMMENT '0=new, 1=recipient click from email, 2=recipient registered, 3=recipient denied invitation',
  verified BIT(1) NULL DEFAULT 0 COMMENT '0=unverified, 1=verified',
  is_primary BIT(1) NULL DEFAULT 0 COMMENT '0=secondary, 1=primary',
  PRIMARY KEY (user_id, email))
ENGINE = InnoDB;

DROP TABLE IF EXISTS SocialAccount_T;
CREATE TABLE SocialAccount_T (
    type_id TINYINT NOT NULL COMMENT '1=FACEBOOK, 2=GOOGLE_PLUS, 3=LINKEDIN, 4=TWITTER, 5=WORDPRESS',
    user_id BIGINT NOT NULL,
    account_id CHAR(50) COMMENT 'social account id',
    social_access_id BIGINT NOT NULL,
    access_token CHAR(255),
    access_type TINYINT NOT NULL COMMENT '1=short lived, 2=long lived, 3=other type',
    name CHAR(255) COMMENT 'social account display name',
    username CHAR(50) COMMENT 'username from social network',
    email CHAR(50) COMMENT 'social account primary email',
    password CHAR(255),
    page_url CHAR(255),
    profile_image_url CHAR(255),
    created DATETIME,
    modified DATETIME,
    active BIT(3) NULL DEFAULT 1 COMMENT '0=inactive, 1=active, 2=inactive by reason 1, 3=inactive by reason 2',
    PRIMARY KEY(user_id, type_id, account_id)
)
ENGINE = innodb;

DROP TABLE IF EXISTS SocialAccess_T;
CREATE TABLE SocialAccess_T (
    id BIGINT NOT NULL auto_increment,
    type_id TINYINT COMMENT '1=FACEBOOK, 2=GOOGLE_PLUS, 3=LINKEDIN, 4=TWITTER, 5=WORDPRESS',
    app_token CHAR(255),
    app_access_secret TINYINT COMMENT '1=short lived, 2=long lived',
    username CHAR(255),
    password CHAR(255),
    PRIMARY KEY(user_id, type_id, account_id)
)
ENGINE = innodb;

DROP TABLE IF EXISTS SocialAccountType_T;
CREATE TABLE SocialAccount_T
    (
        id TINYINT NOT NULL auto_increment,
        active BOOLEAN NOT NULL DEFAULT TRUE,
        description TEXT,
        title TEXT,
        PRIMARY KEY(id)
    )
ENGINE = innodb;

DROP TABLE IF EXISTS `UserPermission_T` ;
CREATE  TABLE UserPermission_T (
  user_id BIGINT NOT NULL,
  permission INT(11) NOT NULL  COMMENT '1=CREATE, 2=READ, 4=UPDATE, 6=READ/UPDATE, 8=DELETE, 3=CREATE/READ, ',
  entity BIGINT NOT NULL,
  entity_type INT(11) NOT NULL,
  PRIMARY KEY (user_id, role_id))
ENGINE = InnoDB;

DROP TABLE IF EXISTS `UserProfile_T` ;
CREATE  TABLE UserProfile_T (
  user_id BIGINT NOT NULL,
  firstname VARCHAR(45) NOT NULL,
  middlename VARCHAR(45) NOT NULL,
  lastname VARCHAR(45) NOT NULL,
  `birth_date` DATE NULL,
  gender CHAR(1) NOT NULL DEFAULT 'M',
  PRIMARY KEY (user_id))
ENGINE = InnoDB;
  
DROP TABLE IF EXISTS `UserRole_T` ;
CREATE  TABLE UserRole_T (
  user_id BIGINT NOT NULL ,
  role_id INT(11) NOT NULL ,
  PRIMARY KEY (user_id, role_id))
ENGINE = InnoDB;

INSERT INTO 
    User_T (username, password)
VALUES 
    (1, 'user1', 'password1'),
    (2, 'user2', 'password2'),
    (3, 'user3', 'password3'),
    (4, 'user4', 'password4'),
    (5, 'user5', 'password5');

INSERT INTO 
    Permission_T (id, permission)
VALUES 
    (1, 'CREATE'),
    (2, 'READ'),
    (4, 'UPDATE'),
    (8, 'DELETE');

INSERT INTO 
    Role_T (id, name)
VALUES 
    (1, 'SYS_ADMIN'),
    (2, 'ADMIN'),
    (3, 'USER');
  
INSERT INTO 
    SocialAccountType_T (id, title, description)
VALUES
    (1, "FACEBOOK", "facebook account"),
    (2, "GOOGLE_PLUS", "google plus account"),
    (3, "LINKEDIN", "linkedin account"),
    (4, "TWITTER", "twitter account"),
    (5, "WORDPRESS", "wordpress account");

INSERT INTO 
    UserRole_T (user_id, role_id)
VALUES 
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 3),
    (5, 3);

INSERT INTO 
    UserEmail_T (user_id, email, active, verified, is_primary)
VALUES 
    (1, 'user1@email1.com', 1, 1, 1),
    (2, 'user2@email1.com', 1, 1, 1),
    (3, 'user3@email1.com', 1, 1, 1),
    (3, 'user3@email2.com', 1, 1, 0),
    (3, 'user3@email3.com', 1, 1, 0),
    (4, 'user4@email1.com', 1, 1, 1),
    (5, 'user4@email1.com', 1, 1, 1);

INSERT INTO 
    UserProfile_T (user_id, firstname, middlename, lastname, birth_date, gender)
VALUES 
    (1, 'firstname1', 'middlename1', 'lastname1', '1991-01-01', 'M'),
    (2, 'firstname2', 'middlename2', 'lastname2', '1992-02-02', 'M'),
    (3, 'firstname3', 'middlename3', 'lastname3', '1993-03-03', 'M'),
    (4, 'firstname4', 'middlename4', 'lastname4', '1994-04-04', 'M'),
    (5, 'firstname5', 'middlename5', 'lastname5', '1995-05-05', 'M');





  

CREATE  TABLE users (
  username VARCHAR(45) NOT NULL ,
  password VARCHAR(45) NOT NULL ,
  enabled TINYINT NOT NULL DEFAULT 1 ,
  PRIMARY KEY (username));

CREATE TABLE user_roles (
  user_role_id int(11) NOT NULL AUTO_INCREMENT,
  username varchar(45) NOT NULL,
  role varchar(45) NOT NULL,
  PRIMARY KEY (user_role_id),
  UNIQUE KEY uni_username_role (role,username),
  KEY fk_username_idx (username),
  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username));
  

INSERT INTO users(username,password,enabled)
VALUES ('admin','admin', true);
INSERT INTO users(username,password,enabled)
VALUES ('admin1','admin1', true);
INSERT INTO users(username,password,enabled)
VALUES ('admin2','admin2', true);
INSERT INTO users(username,password,enabled)
VALUES ('user','user', true);

INSERT INTO user_roles (username, role)
VALUES ('admin', 'ROLE_USER');
INSERT INTO user_roles (username, role)
VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO user_roles (username, role)
VALUES ('admin1', 'ROLE_USER');
INSERT INTO user_roles (username, role)
VALUES ('admin1', 'ROLE_ADMIN');
INSERT INTO user_roles (username, role)
VALUES ('admin2', 'ROLE_USER');
INSERT INTO user_roles (username, role)
VALUES ('admin2', 'ROLE_ADMIN');
INSERT INTO user_roles (username, role)
VALUES ('user', 'ROLE_USER');

CREATE TABLE persistent_logins (
    username varchar(64) not null,
    series varchar(64) not null,
    token varchar(64) not null,
    last_used timestamp not null,
    PRIMARY KEY (series)
);