package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.services;

import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dao.MessageDao;
import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dto.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class MessageManagementService {
    private MessageDao messageDao;

    public MessageManagementService(){
    }

    @Autowired
    public MessageManagementService(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public Message addMessage(Message message) {
        message.setId(UUID.randomUUID());

        int now = (int) java.time.LocalDate.now().toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC);
        message.setCreated(now);

        return messageDao.add(message);
    }

    public List<Message> findByProducerId(String producerId) {
        try {
            UUID uuid = UUID.fromString(producerId);
            return messageDao.findByProducerId(uuid);
        } catch (IllegalArgumentException exception) {
            throw exception;
        }
    }

    public void deleteMessage(String messageId) {
        try {
            UUID uuid = UUID.fromString(messageId);
            messageDao.deleteMessage(uuid);
        } catch (IllegalArgumentException exception) {
            throw exception;
        }
    }

    public List<Message> findAll() {
        return messageDao.findAll();
    }

    public List<Message> findBySubscriberId(String subscriberId) {
        try {
            UUID uuid = UUID.fromString(subscriberId);
            return messageDao.findBySubscriberId(uuid);
        } catch (IllegalArgumentException exception) {
            throw exception;
        }
    }
}
