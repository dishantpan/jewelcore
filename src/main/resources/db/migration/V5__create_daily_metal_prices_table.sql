CREATE TABLE daily_metal_prices (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    metal_id BIGINT NOT NULL,
                                    price_date DATE NOT NULL,
                                    price_per_gram DECIMAL(12,2) NOT NULL,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP,

                                    CONSTRAINT pk_daily_metal_prices PRIMARY KEY (id),

                                    CONSTRAINT fk_daily_metal_price_metal
                                        FOREIGN KEY (metal_id)
                                            REFERENCES metals(id),

                                    CONSTRAINT uk_daily_metal_price_metal_date
                                        UNIQUE (metal_id, price_date)
);