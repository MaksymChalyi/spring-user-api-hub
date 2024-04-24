package com.maksimkaxxl.usermanagementapi.repositories;

import com.maksimkaxxl.usermanagementapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByBirthDateBetween(LocalDate from, LocalDate to);
}
