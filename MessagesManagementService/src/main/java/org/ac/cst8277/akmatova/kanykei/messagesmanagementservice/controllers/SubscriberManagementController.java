package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.controllers;

import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dto.Message;
import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dto.Subscription;
import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.services.MessageManagementService;
import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.services.SubscriptionManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class SubscriberManagementController {
    private static final Logger logger = LogManager.getLogger(SubscriberManagementController.class);
    private MessageManagementService messageManagementService;
    private SubscriptionManagementService subscriptionManagementService;

    public SubscriberManagementController() {
    }

    @Autowired
    public SubscriberManagementController(MessageManagementService messageManagementService,
                                          SubscriptionManagementService subscriptionManagementService) {
        this.messageManagementService = messageManagementService;
        this.subscriptionManagementService = subscriptionManagementService;
    }

    @PostMapping(path = "/subscriptions", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> addSubscription(@RequestBody Subscription newSubscription) {
        logger.info("Create new subscription.");
        try {
            Subscription subscription = subscriptionManagementService.add(newSubscription);

            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(201)
                    .setMessage("Subscription has been created.")
                    .setData(subscription)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception exception) {
            logger.error("SERVER ERROR: " + exception);
            return new ResponseEntity<>(new ApiResponse
                    .Builder()
                    .setCode(500)
                    .setMessage("Server could not process the request.")
                    .setData(null)
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = { "/subscriptions/subscriber/{subscriberId}","/subscriptions/subscriber/" }, produces = "application/json")
    public ResponseEntity<ApiResponse> getSubscriberSubscriptions(@PathVariable("subscriberId") Optional<String> subscriberId) {
        logger.info("Get subscriber subscriptions.");

        if(subscriberId.isEmpty() || subscriberId.get().trim().equals("")) {
            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(401)
                    .setMessage("No token.")
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }

        try {
            List<Subscription> messageList = subscriptionManagementService.findBySubscriberId(subscriberId.get());
            int code;
            String message;

            if (messageList == null || messageList.size() == 0) {
                code = 404;
                message = "Subscriber subscriptions not found or empty.";
            } else {
                code = 200;
                message = "Get subscriptions for specific subscriber.";
            }

            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(code)
                    .setMessage(message)
                    .setData(messageList)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            logger.error("Illegal Argument Exception: " + exception);
            return new ResponseEntity<>(new ApiResponse
                    .Builder()
                    .setCode(403)
                    .setMessage("FORBIDDEN.")
                    .setData(null)
                    .build(), HttpStatus.FORBIDDEN);
        } catch (Exception exception) {
            logger.error("SERVER ERROR: " + exception);
            return new ResponseEntity<>(new ApiResponse
                    .Builder()
                    .setCode(500)
                    .setMessage("Server could not process the request.")
                    .setData(null)
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/subscriptions", consumes = "application/json")
    public ResponseEntity<ApiResponse> removeSubscription(@RequestBody Subscription subscription) {
        logger.info("Delete subscription.");
        try {
            subscriptionManagementService.deleteSubscription(subscription);
            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(204)
                    .setMessage("Subscription has been deleted.")
                    .setData(null)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
        } catch (Exception exception) {
            logger.error("SERVER ERROR: " + exception);
            return new ResponseEntity<>(new ApiResponse
                    .Builder()
                    .setCode(500)
                    .setMessage("Server could not process the request.")
                    .setData(null)
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}