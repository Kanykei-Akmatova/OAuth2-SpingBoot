package org.ac.cst8277.akmatova.kanykei.usermanagementservice.controllers;

import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.Role;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.User;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.services.RoleManagementService;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.services.UserManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserManagementController {
    private static final Logger logger = LogManager.getLogger(UserManagementController.class);
    private UserManagementService userManagementService;
    private RoleManagementService roleManagementService;

    public UserManagementController(){
    }

    @Autowired
    public UserManagementController(UserManagementService userManagementService, RoleManagementService roleManagementService) {
        this.userManagementService = userManagementService;
        this.roleManagementService = roleManagementService;
    }

    @GetMapping(path = "/users", produces = "application/json")
    public ResponseEntity<ApiResponse> getUsers() {
        logger.info("Get all users.");

        try {
            List<User> userList = userManagementService.findAll();
            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(300)
                    .setMessage("Get all users.")
                    .setData(userList)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception exception){
            logger.error("SERVER ERROR: " + exception);
            return new ResponseEntity<>(new ApiResponse
                    .Builder()
                    .setCode(500)
                    .setMessage("Server could not process the request.")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping (path = "/users", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> addUser(@RequestBody User newUser) {
        logger.info("Add user.");
        try {
            newUser = userManagementService.addUser(newUser);

            if (newUser.getRoles().size() > 0) {
                List<Role> roles = roleManagementService.findAll();

                for (Role userRole : newUser.getRoles()) {
                    for (Role role : roles) {
                        if (userRole.getId().compareTo(role.getId()) == 0) {
                            userRole.setName(role.getName());
                            userRole.setDescription(role.getDescription());
                        }
                    }
                }
            }

            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(201)
                    .setMessage("User has been created.")
                    .setData(newUser)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception exception) {
            logger.error("SERVER ERROR: " + exception);
            return new ResponseEntity<>(new ApiResponse
                    .Builder()
                    .setCode(500)
                    .setMessage("Server could not process the request.")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping (path = "/users/{userId}")
    public ResponseEntity<ApiResponse> removeUser(@PathVariable String userId) {
        logger.info("Delete user.");
        try {
            userManagementService.deleteUser(userId);
            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(204)
                    .setMessage("User has been deleted.")
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException exception) {
            logger.error("ArgumentException: " + exception);
            return new ResponseEntity<>(new ApiResponse
                    .Builder()
                    .setCode(400)
                    .setMessage("BAD REQUEST.")
                    .build(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception){
            logger.error("SERVER ERROR: " + exception);
            return new ResponseEntity<>(new ApiResponse
                    .Builder()
                    .setCode(500)
                    .setMessage("Server could not process the request.")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
