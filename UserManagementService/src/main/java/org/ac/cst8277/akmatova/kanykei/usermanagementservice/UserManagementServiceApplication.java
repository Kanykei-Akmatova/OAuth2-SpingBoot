package org.ac.cst8277.akmatova.kanykei.usermanagementservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserManagementServiceApplication {
    private static final Logger logger = LogManager.getLogger(UserManagementServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(UserManagementServiceApplication.class, args);
        logger.info("User Management Service started!");
    }
}
