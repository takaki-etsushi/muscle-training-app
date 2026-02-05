package com.example.muscle_training_app.repository;

import com.example.muscle_training_app.entity.TrainingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TrainingDetailRepository extends JpaRepository<TrainingDetail, Long> {

    @Query("SELECT SUM(d.weight * d.rep * d.setCount) FROM TrainingDetail d " +
            "WHERE d.memo.training.user.userId = :userId " +
            "AND d.memo.training.trainingDay = :date")
    Long sumTotalLoad(@Param("userId") String userId, @Param("date") LocalDate date);

    // ★追加：過去7日間の日ごとの総負荷量を取得するクエリ
    @Query("SELECT d.memo.training.trainingDay as date, SUM(d.weight * d.rep * d.setCount) as total " +
            "FROM TrainingDetail d " +
            "WHERE d.memo.training.user.userId = :userId " +
            "AND d.memo.training.trainingDay > :since " +
            "GROUP BY d.memo.training.trainingDay " +
            "ORDER BY d.memo.training.trainingDay ASC")
    List<Map<String, Object>> getTotalLoadHistory(@Param("userId") String userId, @Param("since") LocalDate since);
}