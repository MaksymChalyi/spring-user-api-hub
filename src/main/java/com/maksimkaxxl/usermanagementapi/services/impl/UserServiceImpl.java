package com.maksimkaxxl.usermanagementapi.services.impl;

import com.maksimkaxxl.usermanagementapi.dtos.UserDto;
import com.maksimkaxxl.usermanagementapi.entities.User;
import com.maksimkaxxl.usermanagementapi.exceptions.UserAlreadyExistsException;
import com.maksimkaxxl.usermanagementapi.exceptions.UserUnderAgeException;
import com.maksimkaxxl.usermanagementapi.repositories.UserRepository;
import com.maksimkaxxl.usermanagementapi.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Value("${user.age.minimum.age}")
    private int minimumUserAge;

    @Transactional
    @Override
    public User createUser(UserDto userDto) {
        Date birthDate = userDto.birthDate();

        if (userRepository.findByEmail(userDto.email()).isEmpty()) {
            if (isOver18(birthDate)) {
                User user = User.builder()
                        .email(userDto.email())
                        .firstName(userDto.firstName())
                        .lastName(userDto.lastName())
                        .birthDate(userDto.birthDate())
                        .address(userDto.address())
                        .phone(userDto.phone())
                        .build();
                userRepository.save(user);
                return user;
            } else {
                throw new UserUnderAgeException("User must be at least 18 years old");
            }
        } else {
            throw new UserAlreadyExistsException("User with email " + userDto.email() + " already exists");
        }
    }

    @Override
    public User updateUser(Long id, UserDto user) {
        return null;
    }

    @Override
    public User updateAllUserFields(Long id, UserDto user) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public List<User> searchUsersByBirthDateRange(Date from, Date to) {
        return null;
    }

    private boolean isOver18(Date birthDate) {
        LocalDate now = LocalDate.now();
        LocalDate birthday = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(birthday, now);
        return period.getYears() >= minimumUserAge;
    }

}
