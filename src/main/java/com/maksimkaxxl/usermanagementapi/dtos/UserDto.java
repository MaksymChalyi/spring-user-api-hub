package com.maksimkaxxl.usermanagementapi.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UserDto(

        @NotEmpty
        @Email
        String email,
        @NotEmpty
        String firstName,
        @NotEmpty
        String lastName,
        @NotEmpty
        @Past
        LocalDate birthDate,
        String address,
        String phone) {
}
