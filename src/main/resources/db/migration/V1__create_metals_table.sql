CREATE TABLE metals (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        name VARCHAR(50) NOT NULL,
                        code VARCHAR(20) NOT NULL,
                        active BOOLEAN NOT NULL DEFAULT TRUE,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                        CONSTRAINT pk_metals PRIMARY KEY (id),
                        CONSTRAINT uk_metals_name UNIQUE (name),
                        CONSTRAINT uk_metals_code UNIQUE (code)
);