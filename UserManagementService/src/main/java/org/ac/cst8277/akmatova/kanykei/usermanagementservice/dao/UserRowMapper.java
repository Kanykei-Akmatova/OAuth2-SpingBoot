package org.ac.cst8277.akmatova.kanykei.usermanagementservice.dao;

import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.User;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.utils.StringUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(UUID.fromString(StringUtil.getUuidStringFromBytes(rs.getBytes("id"))));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setCreated(rs.getInt("created"));

        return user;
    }
}
