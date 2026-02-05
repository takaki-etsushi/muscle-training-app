package com.example.muscle_training_app.repository;

import com.example.muscle_training_app.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

// ユーザーIDはString型なので、<Users, String> となります
public interface UsersRepository extends JpaRepository<Users, String> {
}