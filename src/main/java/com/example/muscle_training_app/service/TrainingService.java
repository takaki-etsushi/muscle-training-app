package com.example.muscle_training_app.service;

import com.example.muscle_training_app.dto.TrainingForm;
import com.example.muscle_training_app.entity.*;
import com.example.muscle_training_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek; // ★追加
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters; // ★追加
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final MemoRepository memoRepository;
    private final TrainingDetailRepository trainingDetailRepository;
    private final ExerciseRepository exerciseRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public void recordTraining(TrainingForm form, String userId) {
        Users user = usersRepository.findById(userId).orElseThrow();
        Exercise exercise = exerciseRepository.findById(form.getExerciseId()).orElseThrow();

        Training training = trainingRepository.findByUserUserIdAndTrainingDay(userId, form.getTrainingDate());
        if (training == null) {
            training = new Training();
            training.setUser(user);
            training.setTrainingDay(form.getTrainingDate());
            trainingRepository.save(training);
        }

        Memo memo = new Memo();
        memo.setTraining(training);
        memo.setExercise(exercise);
        memo.setMemoContent(form.getMemo());
        memoRepository.save(memo);

        TrainingDetail detail = new TrainingDetail();
        detail.setMemo(memo);
        detail.setWeight(form.getWeight());
        detail.setRep(form.getRep());
        detail.setSetCount(form.getSetCount());
        trainingDetailRepository.save(detail);
    }

    public Long getDailyTotalLoad(String userId, LocalDate date) {
        Long total = trainingDetailRepository.sumTotalLoad(userId, date);
        return (total != null) ? total : 0L;
    }

    public List<TrainingDetail> getRecentHistory(String userId) {
        return trainingDetailRepository.findAll().stream()
                .filter(d -> d.getMemo().getTraining().getUser().getUserId().equals(userId))
                .sorted((d1, d2) -> d2.getMemo().getTraining().getTrainingDay().compareTo(d1.getMemo().getTraining().getTrainingDay()))
                .limit(5).toList();
    }

    public List<TrainingDetail> getFilteredHistory(String userId, Long exerciseId) {
        return trainingDetailRepository.findAll().stream()
                .filter(d -> d.getMemo().getTraining().getUser().getUserId().equals(userId))
                .filter(d -> exerciseId == null || d.getMemo().getExercise().getExerciseId().equals(exerciseId))
                .sorted((d1, d2) -> d2.getMemo().getTraining().getTrainingDay().compareTo(d1.getMemo().getTraining().getTrainingDay()))
                .toList();
    }

    @Transactional
    public void deleteTrainingDetail(Long detailId, String userId) {
        TrainingDetail detail = trainingDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("データが見つかりません"));
        if (!detail.getMemo().getTraining().getUser().getUserId().equals(userId)) {
            throw new SecurityException("権限がありません");
        }
        trainingDetailRepository.delete(detail);
    }

    public TrainingDetail getTrainingDetail(Long detailId) {
        return trainingDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("データが見つかりません"));
    }

    @Transactional
    public void updateTrainingDetail(Long detailId, TrainingForm form, String userId) {
        TrainingDetail detail = trainingDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("データが見つかりません"));
        if (!detail.getMemo().getTraining().getUser().getUserId().equals(userId)) {
            throw new SecurityException("権限がありません");
        }

        detail.setWeight(form.getWeight());
        detail.setRep(form.getRep());
        detail.setSetCount(form.getSetCount());

        Memo memo = detail.getMemo();
        memo.setMemoContent(form.getMemo());

        if (!memo.getExercise().getExerciseId().equals(form.getExerciseId())) {
            Exercise newExercise = exerciseRepository.findById(form.getExerciseId()).orElseThrow();
            memo.setExercise(newExercise);
        }

        LocalDate oldDate = memo.getTraining().getTrainingDay();
        LocalDate newDate = form.getTrainingDate();
        if (!oldDate.equals(newDate)) {
            Training newTraining = trainingRepository.findByUserUserIdAndTrainingDay(userId, newDate);
            if (newTraining == null) {
                Users user = usersRepository.findById(userId).orElseThrow();
                newTraining = new Training();
                newTraining.setUser(user);
                newTraining.setTrainingDay(newDate);
                trainingRepository.save(newTraining);
            }
            memo.setTraining(newTraining);
        }
        memoRepository.save(memo);
        trainingDetailRepository.save(detail);
    }

    // ★修正: グラフデータ取得メソッド
    public Map<String, List<?>> getChartData(String userId, String period, Long exerciseId) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        // 1. カレンダー基準の期間設定
        if ("week".equals(period)) {
            // 今週の月曜日 〜 日曜日
            startDate = today.with(DayOfWeek.MONDAY);
            endDate = today.with(DayOfWeek.SUNDAY);
        } else if ("month".equals(period)) {
            // 今月の1日 〜 末日
            startDate = today.with(TemporalAdjusters.firstDayOfMonth());
            endDate = today.with(TemporalAdjusters.lastDayOfMonth());
        } else if ("year".equals(period)) {
            // 今年の1月1日 〜 12月31日
            startDate = today.with(TemporalAdjusters.firstDayOfYear());
            endDate = today.with(TemporalAdjusters.lastDayOfYear());
        } else {
            // 全期間（2020年〜今日）
            startDate = LocalDate.of(2020, 1, 1);
            endDate = today;
        }

        // 2. DBからデータ取得
        List<Map<String, Object>> rawData;
        if (exerciseId != null) {
            rawData = trainingDetailRepository.getWeightHistory(userId, exerciseId, startDate);
        } else {
            rawData = trainingDetailRepository.getTotalLoadHistory(userId, startDate);
        }

        Map<String, Number> dateToValueMap = rawData.stream()
                .collect(Collectors.toMap(
                        m -> m.get("date").toString(),
                        m -> (Number) m.get("val"),
                        (v1, v2) -> v1 // 重複時はそのまま（基本起きない）
                ));

        List<String> labels = new ArrayList<>();
        List<Number> values = new ArrayList<>();

        if ("all".equals(period) || period == null) {
            // 全期間の場合は、データがある日だけを表示（365日x数年分を全て埋めると重すぎるため）
            labels = rawData.stream().map(m -> m.get("date").toString()).collect(Collectors.toList());
            values = rawData.stream().map(m -> (Number) m.get("val")).collect(Collectors.toList());
        } else {
            // week/month/year の場合は、startDateからendDateまで1日ずつループして埋める
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                String dateStr = date.toString();
                labels.add(dateStr);
                // データがあればその値、なければ 0
                values.add(dateToValueMap.getOrDefault(dateStr, 0));
            }
        }

        return Map.of("labels", labels, "values", values);
    }
}