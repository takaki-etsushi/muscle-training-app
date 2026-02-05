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
        // (省略：変更なし)
        Users user = usersRepository.findById(userId).orElseThrow();
        Exercise exercise = exerciseRepository.findById(form.getExerciseId()).orElseThrow();
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
        // (省略：変更なし)
        Long total = trainingDetailRepository.sumTotalLoad(userId, date);
        return (total != null) ? total : 0L;
    }

    public List<TrainingDetail> getRecentHistory(String userId) {
        // (省略：変更なし)
        return trainingDetailRepository.findAll().stream()
                .filter(d -> d.getMemo().getTraining().getUser().getUserId().equals(userId))
                .sorted((d1, d2) -> d2.getMemo().getTraining().getTrainingDay().compareTo(d1.getMemo().getTraining().getTrainingDay()))
                .limit(5).toList();
    }

    // ★修正：期間と種目IDを受け取ってグラフデータを返す
    public Map<String, List<?>> getChartData(String userId, String period, Long exerciseId) {

        // 1. 期間の計算
        LocalDate startDate = LocalDate.now();
        if ("week".equals(period)) {
            startDate = startDate.minusWeeks(1);
        } else if ("month".equals(period)) {
            startDate = startDate.minusMonths(1);
        } else if ("year".equals(period)) {
            startDate = startDate.minusYears(1);
        } else {
            // "all" の場合はずっと昔
            startDate = LocalDate.of(2000, 1, 1);
        }

        // 2. データの取得（種目指定があれば重量推移、なければ総負荷量）
        List<Map<String, Object>> rawData;
        if (exerciseId != null) {
            rawData = trainingDetailRepository.getWeightHistory(userId, exerciseId, startDate);
        } else {
            rawData = trainingDetailRepository.getTotalLoadHistory(userId, startDate);
        }

        List<String> labels = rawData.stream().map(m -> m.get("date").toString()).collect(Collectors.toList());

        // 値の取り出し（Long型かInteger型か不定なためNumberで受ける）
        List<Number> values = rawData.stream().map(m -> (Number) m.get("val")).collect(Collectors.toList());

        return Map.of("labels", labels, "values", values);
    }

    // ★追加：履歴リストの検索・絞り込み用メソッド（簡易実装）
    // 本来はRepositoryで絞り込むべきですが、今回はStreamでフィルタリングします
    public List<TrainingDetail> getFilteredHistory(String userId, Long exerciseId) {
        return trainingDetailRepository.findAll().stream()
                .filter(d -> d.getMemo().getTraining().getUser().getUserId().equals(userId))
                // 種目IDが指定されていればフィルタリング
                .filter(d -> exerciseId == null || d.getMemo().getExercise().getExerciseId().equals(exerciseId))
                .sorted((d1, d2) -> d2.getMemo().getTraining().getTrainingDay().compareTo(d1.getMemo().getTraining().getTrainingDay()))
                .toList();
    }

    // TrainingService.java に以下のメソッドを追加

    // 削除機能
    @Transactional
    public void deleteTrainingDetail(Long detailId, String userId) {
        TrainingDetail detail = trainingDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("データが見つかりません"));

        // 他人のデータを消さないようチェック
        if (!detail.getMemo().getTraining().getUser().getUserId().equals(userId)) {
            throw new SecurityException("権限がありません");
        }

        trainingDetailRepository.delete(detail);
    }

    // 編集用：データの取得
    public TrainingDetail getTrainingDetail(Long detailId) {
        return trainingDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("データが見つかりません"));
    }


    // 編集機能
    @Transactional
    public void updateTrainingDetail(Long detailId, TrainingForm form, String userId) {
        TrainingDetail detail = trainingDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("データが見つかりません"));

        if (!detail.getMemo().getTraining().getUser().getUserId().equals(userId)) {
            throw new SecurityException("権限がありません");
        }

        // 1. 数値の更新
        detail.setWeight(form.getWeight());
        detail.setRep(form.getRep());
        detail.setSetCount(form.getSetCount());

        // 2. メモの更新
        Memo memo = detail.getMemo();
        memo.setMemoContent(form.getMemo());

        // 3. 種目が変更された場合
        if (!memo.getExercise().getExerciseId().equals(form.getExerciseId())) {
            Exercise newExercise = exerciseRepository.findById(form.getExerciseId()).orElseThrow();
            memo.setExercise(newExercise);
        }

        // 4. 日付が変更された場合（少し複雑ですが対応します）
        LocalDate oldDate = memo.getTraining().getTrainingDay();
        LocalDate newDate = form.getTrainingDate();

        if (!oldDate.equals(newDate)) {
            // 新しい日付のTrainingを探す（なければ作る）
            Training newTraining = trainingRepository.findByUserUserIdAndTrainingDay(userId, newDate);
            if (newTraining == null) {
                Users user = usersRepository.findById(userId).orElseThrow();
                newTraining = new Training();
                newTraining.setUser(user);
                newTraining.setTrainingDay(newDate);
                trainingRepository.save(newTraining);
            }
            // 親を付け替える
            memo.setTraining(newTraining);
        }

        // 変更を保存 (JPAの変更検知で自動保存されますが、明示的にsaveしてもOK)
        memoRepository.save(memo);
        trainingDetailRepository.save(detail);
    }
}