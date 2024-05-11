package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.services;

import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dao.SubscriptionDao;
import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dto.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionManagementService {
    private SubscriptionDao subscriptionDao;

    public SubscriptionManagementService(){
    }

    @Autowired
    public SubscriptionManagementService(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }

    public Subscription add(Subscription newSubscription) {
        return subscriptionDao.add(newSubscription);
    }

    public List<Subscription> findBySubscriberId(String subscriberId) {
        try {
            UUID uuid = UUID.fromString(subscriberId);
            return subscriptionDao.findBySubscriberId(uuid);
        } catch (IllegalArgumentException exception) {
            throw exception;
        }
    }

    public void deleteSubscription(Subscription subscription) {
        subscriptionDao.deleteSubscription(subscription);
    }
}
