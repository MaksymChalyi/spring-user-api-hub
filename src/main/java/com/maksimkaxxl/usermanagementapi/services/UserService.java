package com.maksimkaxxl.usermanagementapi.services;

import com.maksimkaxxl.usermanagementapi.dtos.UserDto;
import com.maksimkaxxl.usermanagementapi.entities.User;

import java.util.Date;
import java.util.List;

public interface UserService {

    User createUser(UserDto user);

    User updateUser(Long id, UserDto user);

    User updateAllUserFields(Long id, UserDto user);

    void deleteUser(Long id);

    List<User> searchUsersByBirthDateRange(Date from, Date to);
}
