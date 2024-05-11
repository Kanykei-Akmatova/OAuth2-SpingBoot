package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dao;

import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dto.Message;
import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.util.StringUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MessageRowMapper implements RowMapper<Message> {

    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Message message = new Message();
        message.setId(UUID.fromString(StringUtil.getUuidStringFromBytes(rs.getBytes("id"))));
        message.setContent(rs.getString("content"));
        message.setCreated(rs.getInt("created"));
        message.setAuthor(UUID.fromString(StringUtil.getUuidStringFromBytes(rs.getBytes("producer_id"))));

        return message;
    }
}
