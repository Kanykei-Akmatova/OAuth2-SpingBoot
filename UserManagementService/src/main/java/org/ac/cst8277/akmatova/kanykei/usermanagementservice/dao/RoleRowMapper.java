package org.ac.cst8277.akmatova.kanykei.usermanagementservice.dao;

import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.Role;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.utils.StringUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RoleRowMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        Role role = new Role();
        role.setId(UUID.fromString(StringUtil.getUuidStringFromBytes(rs.getBytes("id"))));
        role.setName(rs.getString("name"));
        role.setDescription(rs.getString("description"));

        return role;
    }
}
