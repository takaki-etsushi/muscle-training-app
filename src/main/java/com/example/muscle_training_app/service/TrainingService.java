package com.example.muscle_training_app.service;

import com.example.muscle_training_app.dto.TrainingForm;
import com.example.muscle_training_app.entity.*;
import com.example.muscle_training_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        Exercise exercise = exerciseRepository.findById(form.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("種目が見つかりません"));

        Training training = new Training();
        training.setUser(user);
        training.setTrainingDay(form.getTrainingDate());
        trainingRepository.save(training);

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
                .sorted((d1, d2) -> d2.getMemo().getTraining().getTrainingDay()
                        .compareTo(d1.getMemo().getTraining().getTrainingDay()))
                .limit(5)
                .toList();
    }

    // ★追加：グラフ描画用のデータ整形メソッド
    public Map<String, List<?>> getChartData(String userId) {
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        List<Map<String, Object>> rawData = trainingDetailRepository.getTotalLoadHistory(userId, weekAgo);

        List<String> labels = rawData.stream()
                .map(m -> m.get("date").toString())
                .collect(Collectors.toList());

        List<Long> values = rawData.stream()
                .map(m -> (Long) m.get("total"))
                .collect(Collectors.toList());

        return Map.of("labels", labels, "values", values);
    }
}