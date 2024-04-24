package com.maksimkaxxl.usermanagementapi.services.impl;

import com.maksimkaxxl.usermanagementapi.dtos.UserDto;
import com.maksimkaxxl.usermanagementapi.entities.User;
import com.maksimkaxxl.usermanagementapi.exceptions.InvalidRequestException;
import com.maksimkaxxl.usermanagementapi.exceptions.UserAlreadyExistsException;
import com.maksimkaxxl.usermanagementapi.exceptions.UserNotFoundException;
import com.maksimkaxxl.usermanagementapi.exceptions.UserUnderAgeException;
import com.maksimkaxxl.usermanagementapi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("Should create user with valid user DTO")
    void createUser_ValidUserDto_ShouldCreateUser() {
        // Arrange
        UserDto userDto = new UserDto("test@example.com", "John", "Doe", LocalDate.of(2000, 1, 1), "123 Main St", "1234567890");
        when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.empty());

        // Act
        User createdUser = userService.createUser(userDto);

        // Assert
        assertNotNull(createdUser);
        assertEquals(userDto.email(), createdUser.getEmail());
        // Add more assertions as needed
    }

    @Test
    @DisplayName("Should throw UserUnderAgeException for user under 18 years old")
    void createUser_UserUnderAge_ShouldThrowUserUnderAgeException() {
        // Arrange
        LocalDate birthDate = LocalDate.now().minusYears(15);
        UserDto userDto = new UserDto("test@example.com", "John", "Doe", birthDate, "123 Main St", "1234567890");

        when(userService.createUser(userDto)).thenThrow(new UserUnderAgeException("User must be at least 18 years old"));
        // Act & Assert
        assertThrows(UserUnderAgeException.class, () -> userService.createUser(userDto));
    }


    @Test
    @DisplayName("Should throw UserAlreadyExistsException for user with existing email")
    void createUser_UserAlreadyExists_ShouldThrowUserAlreadyExistsException() {
        // Arrange
        UserDto userDto = new UserDto("test@example.com", "John", "Doe", LocalDate.of(2000, 1, 1), "123 Main St", "1234567890");
        when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userDto));
    }

    @Test
    @DisplayName("Should update user with valid user DTO")
    void updateUser_ValidUserDto_ShouldUpdateUser() {
        // Arrange
        Long userId = 1L;
        UserDto userDto = new UserDto("updated@example.com", "Updated", "User", LocalDate.of(1990, 1, 1), "456 Updated St", "0987654321");
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User updatedUser = userService.updateUser(userId, userDto);

        // Assert
        assertEquals(userId, updatedUser.getId());
        assertEquals(userDto.email(), updatedUser.getEmail());
        assertEquals(userDto.firstName(), updatedUser.getFirstName());
        assertEquals(userDto.lastName(), updatedUser.getLastName());
        assertEquals(userDto.birthDate(), updatedUser.getBirthDate());
        assertEquals(userDto.address(), updatedUser.getAddress());
        assertEquals(userDto.phone(), updatedUser.getPhone());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user to update is not found")
    void updateUser_UserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        Long userId = 1L;
        UserDto userDto = new UserDto("updated@example.com", "Updated", "User", LocalDate.of(1990, 1, 1), "456 Updated St", "0987654321");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, userDto));
    }

    @Test
    @DisplayName("Should update all user fields with valid user DTO")
    void updateAllUserFields_ValidUserDto_ShouldUpdateAllUserFields() {
        // Arrange
        Long userId = 1L;
        UserDto userDto = new UserDto("someEmail@gmail.com", "Updated", "User", LocalDate.of(1990, 1, 1), "456 Updated St", null);
        User existingUser = new User();
        existingUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User updatedUser = userService.updateAllUserFields(userId, userDto);

        // Assert
        assertEquals(userId, updatedUser.getId());
        assertNull(updatedUser.getEmail());
        assertEquals(userDto.firstName(), updatedUser.getFirstName());
        assertEquals(userDto.lastName(), updatedUser.getLastName());
        assertEquals(userDto.birthDate(), updatedUser.getBirthDate());
        assertEquals(userDto.address(), updatedUser.getAddress());
        assertNull(updatedUser.getPhone());
    }


    @Test
    @DisplayName("Should throw UserNotFoundException when user to update all fields is not found")
    void updateAllUserFields_UserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        Long userId = 1L;
        UserDto userDto = new UserDto(null, "Updated", "User", LocalDate.of(1990, 1, 1), "456 Updated St", null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateAllUserFields(userId, userDto));
    }

    @Test
    @DisplayName("Should delete user when user exists")
    void deleteUser_UserExists_ShouldDeleteUser() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user to delete is not found")
    void deleteUser_UserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    @DisplayName("Should return list of users for valid birth date range")
    void searchUsersByBirthDateRange_ValidDates_ShouldReturnListOfUsers() {
        // Arrange
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 1, 1);
        when(userRepository.findByBirthDateBetween(fromDate, toDate)).thenReturn(List.of(new User(), new User()));

        // Act
        List<User> users = userService.searchUsersByBirthDateRange(fromDate, toDate);

        // Assert
        assertFalse(users.isEmpty());
        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("Should throw InvalidRequestException when from date is after to date")
    void searchUsersByBirthDateRange_FromDateAfterToDate_ShouldThrowInvalidRequestException() {
        // Arrange
        LocalDate fromDate = LocalDate.of(2000, 1, 1);
        LocalDate toDate = LocalDate.of(1990, 1, 1);

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> userService.searchUsersByBirthDateRange(fromDate, toDate));
    }


}



