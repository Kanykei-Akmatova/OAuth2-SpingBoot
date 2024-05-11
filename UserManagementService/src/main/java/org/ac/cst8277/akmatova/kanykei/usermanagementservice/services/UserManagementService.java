package org.ac.cst8277.akmatova.kanykei.usermanagementservice.services;

import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dao.UserDao;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.Role;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.User;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class UserManagementService {

    private UserDao userDao;

    public UserManagementService(){
    }

    @Autowired
    public UserManagementService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> findAll() {
        List<User> users = userDao.findAll();
        HashMap<UUID, List<Role>> usersRoles = userDao.findUsersRoles();

        for (User user:users) {
            user.setRoles(usersRoles.get(user.getId()));
        }
        
        return users;
    }

    public User addUser(User newUser) {
        newUser.setId(UUID.randomUUID());

        int now = (int) java.time.LocalDate.now().toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC);
        newUser.setCreated(now);
        UUID userId = userDao.add(newUser).getId();

        for (Role role : newUser.getRoles()) {
            UserRole newUserRole = new UserRole();
            newUserRole.setUserId(userId);
            newUserRole.setRoleId(role.getId());

            userDao.addUserRole(newUserRole);
        }

        return newUser;
    }

    public void deleteUser(String userId) {
        try {
            UUID uuid = UUID.fromString(userId);
            userDao.deleteUser(uuid);
        } catch (IllegalArgumentException exception) {
            throw exception;
        }
    }

    public User findByEmail(String email) {
        var user = userDao.findByEmail(email);
        if (null != user) {
            HashMap<UUID, List<Role>> usersRoles = userDao.findUsersRoles();
            user.setRoles(usersRoles.get(user.getId()));
            return user;
        }
        throw new UsernameNotFoundException("User not found");
    }
}
