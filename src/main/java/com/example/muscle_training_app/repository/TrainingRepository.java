package com.example.muscle_training_app.repository;

import com.example.muscle_training_app.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    // ★追加: ユーザーIDと日付でトレーニング親データを検索するメソッド
    Training findByUserUserIdAndTrainingDay(String userId, LocalDate trainingDay);
}