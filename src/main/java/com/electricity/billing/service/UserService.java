package com.electricity.billing.service;

import org.springframework.stereotype.Service;

import com.electricity.billing.dao.UserDao;
import com.electricity.billing.model.User;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void createUser(User user) {
        userDao.saveUser(user);
    }
    
    public User getUserByServiceId(Long serviceId) {
        return userDao.findByServiceId(serviceId);
    }
}