CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE TABLE rentals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    surface INT NOT NULL,
    price INT NOT NULL,
    description TEXT,
    picture_path VARCHAR(500) NOT NULL,
    owner_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_rentals_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);
CREATE INDEX idx_rentals_owner_id ON rentals(owner_id);

CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    message TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    rental_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_messages_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_messages_rental FOREIGN KEY (rental_id) REFERENCES rentals (id)
);
CREATE INDEX idx_messages_rental_id ON messages(rental_id);
CREATE INDEX idx_messages_user_id ON messages(user_id);
