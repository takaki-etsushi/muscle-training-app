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

    // ★修正：期間 (:startDate) を指定して総負荷量を取得
    @Query("SELECT d.memo.training.trainingDay as date, SUM(d.weight * d.rep * d.setCount) as val " +
            "FROM TrainingDetail d " +
            "WHERE d.memo.training.user.userId = :userId " +
            "AND d.memo.training.trainingDay >= :startDate " +
            "GROUP BY d.memo.training.trainingDay " +
            "ORDER BY d.memo.training.trainingDay ASC")
    List<Map<String, Object>> getTotalLoadHistory(@Param("userId") String userId, @Param("startDate") LocalDate startDate);

    // ★追加：種目ごとの最大重量推移を取得（種目別グラフ用）
    @Query("SELECT d.memo.training.trainingDay as date, MAX(d.weight) as val " +
            "FROM TrainingDetail d " +
            "WHERE d.memo.training.user.userId = :userId " +
            "AND d.memo.exercise.exerciseId = :exerciseId " +
            "AND d.memo.training.trainingDay >= :startDate " +
            "GROUP BY d.memo.training.trainingDay " +
            "ORDER BY d.memo.training.trainingDay ASC")
    List<Map<String, Object>> getWeightHistory(@Param("userId") String userId,
                                               @Param("exerciseId") Long exerciseId,
                                               @Param("startDate") LocalDate startDate);
}