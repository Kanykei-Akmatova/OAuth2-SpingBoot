package org.ac.cst8277.akmatova.kanykei.usermanagementservice.dao;

import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.Role;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.User;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.UserRole;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.utils.ByteBufferUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class UserDao {
    private static final String FIND_ALL =
            "SELECT id, name, password, email, created " +
            "FROM users " +
            "ORDER BY name;";
    private static final String INSERT =
            "INSERT INTO users (id, name, password, email, created) " +
            "VALUES(?, ?, ?, ?, ?);";
    private static final String DELETE = "DELETE FROM users WHERE id = ?;";

    private static final String FIND_USERS_ROLES =
            "SELECT r.name, r.description, ur.users_id,ur.roles_id " +
            "FROM roles r " +
            "INNER JOIN users_has_roles ur ON r.id = ur.roles_id;";

    private static final String INSERT_USERS_ROLES =
            "INSERT INTO users_has_roles (users_id, roles_id) " +
            "VALUES(?, ?);";

    private static final String FIND_BY_EMAIL =
            "SELECT id, name, password, email, created " +
                    "FROM users " +
                    "WHERE email = ?;";
    private JdbcTemplate jdbc;

    public UserDao(){
    }

    @Autowired
    public UserDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<User> findAll() {
        return jdbc.query(FIND_ALL, new UserRowMapper());
    }
    public HashMap<UUID, List<Role>> findUsersRoles() {
        HashMap<UUID, List<Role>> usersRoles = new HashMap<>();
        List<UserRole> userRoleList = jdbc.query(FIND_USERS_ROLES, new UserRoleRowMapper());

        for (UserRole userRole: userRoleList) {
            List<Role> roles;
            if(usersRoles.containsKey(userRole.getUserId())){
                roles = usersRoles.get(userRole.getUserId());
            } else {
                roles = new ArrayList<>();
            }
            roles.add(new Role(userRole.getRoleId(), userRole.getName(), userRole.getDescription()));
            usersRoles.put(userRole.getUserId(), roles);
        }

        return usersRoles;
    }

    public User add(User newUser) {
        jdbc.execute(INSERT,
                (PreparedStatementCallback<Boolean>) ps -> {
                    ps.setBytes(1, ByteBufferUtil.getByteBuffer(newUser.getId()).array());
                    ps.setString(2, newUser.getName());
                    ps.setString(3, newUser.getPassword());
                    ps.setString(4, newUser.getEmail());
                    ps.setInt(5, newUser.getCreated());
                    return ps.execute();
                });

        return newUser;
    }

    public UserRole addUserRole(UserRole newUserRole) {
        jdbc.execute(INSERT_USERS_ROLES,
                (PreparedStatementCallback<Boolean>) ps -> {
                    ps.setBytes(1, ByteBufferUtil.getByteBuffer(newUserRole.getUserId()).array());
                    ps.setBytes(2, ByteBufferUtil.getByteBuffer(newUserRole.getRoleId()).array());
                    return ps.execute();
                });

        return newUserRole;
    }

    public void deleteUser(UUID uuid) {
        jdbc.update(DELETE, new Object[]{ByteBufferUtil.getByteBuffer(uuid).array()});
    }

    public User findByEmail(String email) {
        var users = jdbc.query(FIND_BY_EMAIL, new UserRowMapper(), new Object[]{ email });
        if (users.size() > 0) {
            return users.get(0);
        }

        return null;
    }
}
