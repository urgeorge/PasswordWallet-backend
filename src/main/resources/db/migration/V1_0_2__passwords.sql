CREATE TABLE IF NOT EXISTS passwords (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    website_name VARCHAR(50) NOT NULL,
    password VARCHAR(256) NOT NULL,
    description VARCHAR(256) DEFAULT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

ALTER TABLE passwords
    ADD CONSTRAINT fk_passwords_users
    FOREIGN KEY (user_id)
    REFERENCES passwordwallet.users(id);
