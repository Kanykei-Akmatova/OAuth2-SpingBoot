package org.ac.cst8277.akmatova.kanykei.usermanagementservice.controllers;

import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.Role;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.services.RoleManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoleManagementController {
    private static final Logger logger = LogManager.getLogger(RoleManagementController.class);

    private RoleManagementService roleManagementService;

    public RoleManagementController(){
    }
    @Autowired
    public RoleManagementController(RoleManagementService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    @GetMapping(path = "/roles", produces = "application/json")
    public ResponseEntity<ApiResponse> getRoles() {
        logger.info("Get all roles.");

        try {
            List<Role> roleList = roleManagementService.findAll();
            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(200)
                    .setMessage("Get all roles.")
                    .setData(roleList)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception exception){
            return new ResponseEntity<>(new ApiResponse
                    .Builder()
                    .setCode(500)
                    .setMessage("Server could not process the request.")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
