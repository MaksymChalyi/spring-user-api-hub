package com.maksimkaxxl.usermanagementapi.controllers;

import com.maksimkaxxl.usermanagementapi.dtos.UserDto;
import com.maksimkaxxl.usermanagementapi.entities.User;
import com.maksimkaxxl.usermanagementapi.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateAllUserFields(id, userDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateAllUserFields(@PathVariable Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByBirthDateRange(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        List<User> users = userService.searchUsersByBirthDateRange(from, to);
        return ResponseEntity.ok(users);
    }


}
