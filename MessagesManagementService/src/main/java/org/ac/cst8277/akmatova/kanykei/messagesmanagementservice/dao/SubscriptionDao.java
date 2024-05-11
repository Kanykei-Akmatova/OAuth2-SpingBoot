package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dao;

import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dto.Subscription;
import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.util.ByteBufferUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class SubscriptionDao {
    private static final String INSERT = "INSERT INTO subscriptions (producers_id, subscribers_id) VALUES(?, ?);";
    private static final String DELETE = "DELETE FROM subscriptions WHERE producers_id = ? AND subscribers_id = ?;";
    private static final String FIND_ALL_BY_SUBSCRIBER =
            "SELECT producers_id, subscribers_id  " +
            "FROM messages.subscriptions " +
                    "WHERE subscribers_id = ?;";
    private JdbcTemplate jdbc;

    public SubscriptionDao(){
    }

    @Autowired
    public SubscriptionDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Subscription add(Subscription subscription) {
        jdbc.execute(INSERT,
                (PreparedStatementCallback<Boolean>) ps -> {
                    ps.setBytes(1, ByteBufferUtil.getByteBuffer(subscription.getAuthor()).array());
                    ps.setBytes(2, ByteBufferUtil.getByteBuffer(subscription.getSubscriber()).array());
                    return ps.execute();
                });

        return subscription;
    }

    public List<Subscription> findBySubscriberId(UUID uuid) {
        return jdbc.query(FIND_ALL_BY_SUBSCRIBER,
                new SubscriptionRowMapper(),
                new Object[]{ByteBufferUtil.getByteBuffer(uuid).array()});
    }

    public void deleteSubscription(Subscription subscription) {
        jdbc.update(DELETE,
                ByteBufferUtil.getByteBuffer(subscription.getAuthor()).array(),
                ByteBufferUtil.getByteBuffer(subscription.getSubscriber()).array());
    }
}
