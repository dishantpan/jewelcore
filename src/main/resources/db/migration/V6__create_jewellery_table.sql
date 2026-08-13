CREATE TABLE jewellery (
                           id BIGINT NOT NULL AUTO_INCREMENT,

                           sku VARCHAR(50) NOT NULL,
                           name VARCHAR(150) NOT NULL,
                           description VARCHAR(500),

                           category_id BIGINT NOT NULL,
                           metal_id BIGINT NOT NULL,
                           purity_id BIGINT NOT NULL,

                           gross_weight DECIMAL(10,3) NOT NULL,
                           stone_weight DECIMAL(10,3) NOT NULL,
                           net_weight DECIMAL(10,3) NOT NULL,

                           making_charge DECIMAL(12,2) NOT NULL,

                           active BOOLEAN NOT NULL DEFAULT TRUE,

                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,

                           CONSTRAINT pk_jewellery
                               PRIMARY KEY (id),

                           CONSTRAINT uk_jewellery_sku
                               UNIQUE (sku),

                           CONSTRAINT fk_jewellery_category
                               FOREIGN KEY (category_id)
                                   REFERENCES categories(id),

                           CONSTRAINT fk_jewellery_metal
                               FOREIGN KEY (metal_id)
                                   REFERENCES metals(id),

                           CONSTRAINT fk_jewellery_purity
                               FOREIGN KEY (purity_id)
                                   REFERENCES purities(id)
);