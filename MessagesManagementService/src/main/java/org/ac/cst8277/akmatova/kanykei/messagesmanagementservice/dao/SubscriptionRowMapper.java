package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dao;

import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.dto.Subscription;
import org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.util.StringUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SubscriptionRowMapper implements RowMapper<Subscription> {

    @Override
    public Subscription mapRow(ResultSet rs, int rowNum) throws SQLException {
        Subscription Subscription = new Subscription();
        Subscription.setAuthor(UUID.fromString(StringUtil.getUuidStringFromBytes(rs.getBytes("producers_id"))));
        Subscription.setSubscriber(UUID.fromString(StringUtil.getUuidStringFromBytes(rs.getBytes("subscribers_id"))));

        return Subscription;
    }
}
