package org.ac.cst8277.akmatova.kanykei.usermanagementservice.services;

import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dao.RoleDao;
import org.ac.cst8277.akmatova.kanykei.usermanagementservice.dto.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleManagementService {
    private RoleDao roleDao;

    public RoleManagementService(){
    }

    @Autowired
    public RoleManagementService(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public List<Role> findAll() {
        return roleDao.findAll();
    }
}
