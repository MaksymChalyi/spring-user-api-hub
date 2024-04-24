package com.maksimkaxxl.usermanagementapi.services.impl;

import com.maksimkaxxl.usermanagementapi.dtos.UserDto;
import com.maksimkaxxl.usermanagementapi.entities.User;
import com.maksimkaxxl.usermanagementapi.exceptions.UserAlreadyExistsException;
import com.maksimkaxxl.usermanagementapi.exceptions.UserNotFoundException;
import com.maksimkaxxl.usermanagementapi.exceptions.UserUnderAgeException;
import com.maksimkaxxl.usermanagementapi.repositories.UserRepository;
import com.maksimkaxxl.usermanagementapi.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
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
        var birthDate = userDto.birthDate();

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
    @Transactional
    public User updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (userDto.email() != null) {
            user.setEmail(userDto.email());
        }
        if (userDto.firstName() != null) {
            user.setFirstName(userDto.firstName());
        }
        if (userDto.lastName() != null) {
            user.setLastName(userDto.lastName());
        }
        if (userDto.birthDate() != null) {
            user.setBirthDate(userDto.birthDate());
        }
        if (userDto.address() != null) {
            user.setAddress(userDto.address());
        }
        if (userDto.phone() != null) {
            user.setPhone(userDto.phone());
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateAllUserFields(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setBirthDate(userDto.birthDate());
        user.setAddress(userDto.address());
        user.setPhone(userDto.phone());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

    }

    @Override
    public List<User> searchUsersByBirthDateRange(Date from, Date to) {
        return null;
    }

    private boolean isOver18(LocalDate birthDate) {
        LocalDate now = LocalDate.now();
        int age = Period.between(birthDate, now).getYears();
        return age >= minimumUserAge;
    }


}
