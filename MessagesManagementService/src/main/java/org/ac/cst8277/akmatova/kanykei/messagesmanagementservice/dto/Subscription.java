package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    private UUID author;
    private UUID subscriber;

    public UUID getAuthor() {
        return author;
    }

    public void setAuthor(UUID author) {
        this.author = author;
    }

    public UUID getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(UUID subscriber) {
        this.subscriber = subscriber;
    }
}
