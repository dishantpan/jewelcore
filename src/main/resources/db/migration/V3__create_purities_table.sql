CREATE TABLE purities (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          name VARCHAR(100) NOT NULL,
                          code VARCHAR(20) NOT NULL,
                          percentage DECIMAL(5,2) NOT NULL,
                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT pk_purities PRIMARY KEY (id),
                          CONSTRAINT uk_purities_code UNIQUE (code)
);