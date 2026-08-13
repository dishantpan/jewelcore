CREATE TABLE inventory_items (
                                 id BIGINT NOT NULL AUTO_INCREMENT,

                                 item_code VARCHAR(50) NOT NULL,
                                 jewellery_id BIGINT NOT NULL,

                                 gross_weight DECIMAL(10,3) NOT NULL,
                                 stone_weight DECIMAL(10,3) NOT NULL,
                                 net_weight DECIMAL(10,3) NOT NULL,

                                 purchase_cost DECIMAL(12,2) NOT NULL,

                                 status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',

                                 location VARCHAR(100),

                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                     ON UPDATE CURRENT_TIMESTAMP,

                                 CONSTRAINT pk_inventory_items
                                     PRIMARY KEY (id),

                                 CONSTRAINT uk_inventory_item_code
                                     UNIQUE (item_code),

                                 CONSTRAINT fk_inventory_item_jewellery
                                     FOREIGN KEY (jewellery_id)
                                         REFERENCES jewellery(id)
);