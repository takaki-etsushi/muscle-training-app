package com.example.muscle_training_app.repository;

import com.example.muscle_training_app.entity.BodyPart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyPartRepository extends JpaRepository<BodyPart, String> {
}