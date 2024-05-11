package org.ac.cst8277.akmatova.kanykei.usermanagementservice.dao;

import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDao {
    private static final String FIND_ALL = "SELECT id, name, description FROM roles;";
    private JdbcTemplate jdbc;

    public RoleDao(){
    }

    @Autowired
    public RoleDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Role> findAll() {
        return jdbc.query(FIND_ALL, new RoleRowMapper());
    }
}
