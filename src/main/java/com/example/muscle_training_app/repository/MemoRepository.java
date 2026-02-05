package com.example.muscle_training_app.repository;

import com.example.muscle_training_app.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<Memo, Long> {
}