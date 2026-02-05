package com.example.muscle_training_app.repository;

import com.example.muscle_training_app.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}