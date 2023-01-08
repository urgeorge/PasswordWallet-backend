CREATE TABLE IF NOT EXISTS last_login_info (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    last_success_attempt TIMESTAMP NOT NULL,
    last_fail_attempt TIMESTAMP NOT NULL
    DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

ALTER TABLE last_login_info
    ADD CONSTRAINT fk_last_login_info_users
    FOREIGN KEY (user_id)
    REFERENCES passwordwallet.users(id);
