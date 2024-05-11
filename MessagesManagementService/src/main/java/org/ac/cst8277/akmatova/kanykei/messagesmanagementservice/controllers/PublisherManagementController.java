package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.controllers;

import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dto.Message;
import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.services.MessageManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class PublisherManagementController {
    private static final Logger logger = LogManager.getLogger(PublisherManagementController.class);
    private MessageManagementService messageManagementService;

    public PublisherManagementController(){
    }

    @Autowired
    public PublisherManagementController(MessageManagementService messageManagementService) {
        this.messageManagementService = messageManagementService;
    }

    @PostMapping (path = "/messages", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> publishMessage(@RequestBody Message newMessage) {
        logger.info("Publishing Message for " + newMessage.getAuthor() +" Producer");
        try {
            Message message = messageManagementService.addMessage(newMessage);
            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(201)
                    .setMessage("Get messages for specific subscriber.")
                    .setData(message)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception exception){
            logger.error("SERVER ERROR: " + exception);
            return new ResponseEntity<>(new ApiResponse
                    .Builder()
                    .setCode(500)
                    .setMessage("Server could not process the request.")
                    .setData(null)
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/messages", produces = "application/json")
    public ResponseEntity<ApiResponse> getMessages() {
        logger.info("Get all messages.");

        try {
            List<Message> messageList = messageManagementService.findAll();
            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(200)
                    .setMessage("Get all messages.")
                    .setData(messageList)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
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

    @DeleteMapping (path = {"/messages/{messageId}", "/messages/"})
    public ResponseEntity<ApiResponse> removeMessage(@PathVariable Optional<String> messageId) {
        logger.info("Delete message.");

        if (messageId.isEmpty() || messageId.get().trim().equals("")) {
            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(401)
                    .setMessage("No token.")
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }

        try {
            messageManagementService.deleteMessage(messageId.get());
            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(204)
                    .setMessage("Message has been deleted.")
                    .setData(null)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
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

    @GetMapping(path = { "/messages/producer/{producerId}","/messages/producer/" }, produces = "application/json")
    public ResponseEntity<ApiResponse> getProducerMessages(@PathVariable("producerId") Optional<String> producerId) {
        logger.info("Get messages produces by specific producer.");

        if(producerId.isEmpty() || producerId.get().trim().equals("")) {
            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(401)
                    .setMessage("No token.")
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }

        try {
            List<Message> messageList = messageManagementService.findByProducerId(producerId.get());
            int code;
            String message;

            if (messageList == null || messageList.size() == 0) {
                code = 404;
                message = "Producer messages not found or empty.";
            } else {
                code = 200;
                message = "Get messages produces by specific producer.";
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

    @GetMapping(path ={ "/messages/subscriber/{subscriberId}","/messages/subscriber/" } , produces = "application/json")
    public ResponseEntity<ApiResponse> getSubscriberMessages(@PathVariable("subscriberId") Optional<String> subscriberId) {
        logger.info("Get messages for specific subscriber.");

        if(subscriberId.isEmpty() || subscriberId.get().trim().equals("")) {
            ApiResponse apiResponse = new ApiResponse
                    .Builder()
                    .setCode(401)
                    .setMessage("No token.")
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }

        try {
            List<Message> messageList = messageManagementService.findBySubscriberId(subscriberId.get());
            int code;
            String message;

            if (messageList == null || messageList.size() == 0) {
                code = 404;
                message = "Subscriber messages not found or empty.";
            } else {
                code = 200;
                message = "Get messages for specific subscriber.";
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

}