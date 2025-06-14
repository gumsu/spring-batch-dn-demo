CREATE TABLE restaurant (
                            base_dt VARCHAR(8),
                            sequence BIGINT(100),
                            open_service_name VARCHAR(100),
                            open_service_id VARCHAR(100),
                            municipality_code VARCHAR(100),
                            management_number VARCHAR(100),
                            permission_date VARCHAR(100),
                            cancellation_date VARCHAR(100),
                            business_status_code VARCHAR(100),
                            business_status_name VARCHAR(100),
                            detailed_business_status_code VARCHAR(100),
                            detailed_business_status_name VARCHAR(100),
                            closure_date VARCHAR(100),
                            suspension_start_date VARCHAR(100),
                            suspension_end_date VARCHAR(100),
                            reopen_date VARCHAR(100),
                            contact_number VARCHAR(100),
                            site_area VARCHAR(100),
                            postal_code VARCHAR(100),
                            full_address VARCHAR(100),
                            road_name_address VARCHAR(100),
                            road_name_postal_code VARCHAR(100),
                            business_name VARCHAR(100),
                            last_modified_date VARCHAR(100),
                            data_update_type VARCHAR(100),
                            data_update_date VARCHAR(100),
                            business_category VARCHAR(100),
                            coordinate_x VARCHAR(100),
                            coordinate_y VARCHAR(100),
                            sanitation_type VARCHAR(100),
                            number_of_male_employees VARCHAR(100),
                            number_of_female_employees VARCHAR(100),
                            business_area_type VARCHAR(100),
                            grade VARCHAR(100),
                            water_supply_type VARCHAR(100),
                            total_employees VARCHAR(100),
                            head_office_employees VARCHAR(100),
                            factory_office_employees VARCHAR(100),
                            factory_sales_employees VARCHAR(100),
                            factory_production_employees VARCHAR(100),
                            building_ownership_type VARCHAR(100),
                            security_deposit VARCHAR(100),
                            monthly_rent VARCHAR(100),
                            is_multi_use_facility VARCHAR(100),
                            total_facility_size VARCHAR(100),
                            traditional_business_designation_number VARCHAR(100),
                            main_dish_of_traditional_business VARCHAR(100),
                            homepage VARCHAR(100),
                            PRIMARY KEY (base_dt, sequence)
);

CREATE TABLE batch_execution_log (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     job_name VARCHAR(100) NOT NULL,
                                     job_execution_id BIGINT,
                                     step_name VARCHAR(100),
                                     status VARCHAR(100),
                                     start_time DATETIME,
                                     end_time DATETIME,
                                     exit_code VARCHAR(100),
                                     exit_message TEXT,
                                     read_count INT DEFAULT 0,
                                     write_count INT DEFAULT 0,
                                     commit_count INT DEFAULT 0,
                                     rollback_count INT DEFAULT 0,
                                     error_message TEXT,
                                     created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
