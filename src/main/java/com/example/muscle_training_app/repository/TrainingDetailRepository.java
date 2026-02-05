package com.example.muscle_training_app.repository;

import com.example.muscle_training_app.entity.TrainingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface TrainingDetailRepository extends JpaRepository<TrainingDetail, Long> {

    // JPQLという専用の言語で「重量 × 回数 × セット数」の合計を計算します
    @Query("SELECT SUM(d.weight * d.rep * d.setCount) FROM TrainingDetail d " +
            "WHERE d.memo.training.user.userId = :userId " +
            "AND d.memo.training.trainingDay = :date")
    Long sumTotalLoad(@Param("userId") String userId, @Param("date") LocalDate date);
}