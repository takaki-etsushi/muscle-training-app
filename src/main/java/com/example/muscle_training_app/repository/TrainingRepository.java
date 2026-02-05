package com.example.muscle_training_app.repository;

import com.example.muscle_training_app.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingRepository extends JpaRepository<Training, Long> {
}