package org.ac.cst8277.akmatova.kanykei.usermanagementservice.dao;

import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.UserRole;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.utils.StringUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRoleRowMapper implements RowMapper<UserRole> {

    @Override
    public UserRole mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserRole userRole = new UserRole();
        userRole.setUserId(UUID.fromString(StringUtil.getUuidStringFromBytes(rs.getBytes("users_id"))));
        userRole.setRoleId(UUID.fromString(StringUtil.getUuidStringFromBytes(rs.getBytes("roles_id"))));
        userRole.setName(rs.getString("name"));
        userRole.setDescription(rs.getString("description"));

        return userRole;
    }
}
