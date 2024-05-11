package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dao;

import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dto.Message;
import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.util.ByteBufferUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class MessageDao {
    private static final String INSERT = "INSERT INTO messages (id, content, created, producer_id) VALUES(?, ?, ? ,?);";
    private static final String FIND_ALL_BY_PRODUCER =
            "SELECT m.id, content, created, producer_id " +
                    "FROM messages m " +
                    "WHERE producer_id = ?;";
    private static final String FIND_ALL = "SELECT id, content, created, producer_id FROM messages;";
    private static final String FIND_ALL_BY_SUBSCRIBER =
            "SELECT m.id, content, created, producer_id " +
                    "FROM messages m " +
                    "INNER JOIN subscriptions ss ON m.producer_id = ss.producers_id " +
                    "INNER JOIN subscribers s ON ss.subscribers_id = s.id " +
                    "WHERE s.id = ?;";
    private static final String DELETE = "DELETE FROM messages WHERE id = ?;";
    private JdbcTemplate jdbc;

    public MessageDao(){
    }

    @Autowired
    public MessageDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Message add(Message message) {
        jdbc.execute(INSERT,
                (PreparedStatementCallback<Boolean>) ps -> {
                    ps.setBytes(1, ByteBufferUtil.getByteBuffer(message.getId()).array());
                    ps.setString(2, message.getContent());
                    ps.setInt(3, message.getCreated());
                    ps.setBytes(4, ByteBufferUtil.getByteBuffer(message.getAuthor()).array());
            return ps.execute();
        });

        return message;
    }

    public List<Message> findByProducerId(UUID producerId) {
        return jdbc.query(FIND_ALL_BY_PRODUCER,
                new MessageRowMapper(),
                new Object[]{ByteBufferUtil.getByteBuffer(producerId).array()});
    }

    public void deleteMessage(UUID messageId) {
        jdbc.update(DELETE, new Object[]{ByteBufferUtil.getByteBuffer(messageId).array()});
    }

    public List<Message> findAll() {
        return jdbc.query(FIND_ALL, new MessageRowMapper());
    }

    public List<Message> findBySubscriberId(UUID subscriberId) {
        return jdbc.query(FIND_ALL_BY_SUBSCRIBER,
                new MessageRowMapper(),
                new Object[]{ByteBufferUtil.getByteBuffer(subscriberId).array()});
    }
}
