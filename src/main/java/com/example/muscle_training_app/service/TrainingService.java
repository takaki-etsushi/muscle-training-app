package com.example.muscle_training_app.service;

import com.example.muscle_training_app.dto.TrainingForm;
import com.example.muscle_training_app.entity.*;
import com.example.muscle_training_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final MemoRepository memoRepository;
    private final TrainingDetailRepository trainingDetailRepository;
    private final ExerciseRepository exerciseRepository;
    private final UsersRepository usersRepository;

    // 画面から受け取ったフォームデータを使って保存するメソッド
    @Transactional // 途中でエラーが出たら全部取り消してくれる（データの整合性を保つ）
    public void recordTraining(TrainingForm form, String userId) {

        // 1. ログイン中のユーザーを取得
        // ★変更：固定の "user" ではなく、引数の userId を使う
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        // 2. 種目を取得
        Exercise exercise = exerciseRepository.findById(form.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("種目が見つかりません"));

        // ... (以下は変更なし) ...
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

    // ★追加：指定した日付の総負荷量を取得する
    public Long getDailyTotalLoad(String userId, LocalDate date) {
        Long total = trainingDetailRepository.sumTotalLoad(userId, date);
        // データがない場合(null)は0を返す
        return (total != null) ? total : 0L;
    }

    // ★追加：最近のトレーニング履歴を少しだけ取得する（ホーム画面用）
    public java.util.List<TrainingDetail> getRecentHistory(String userId) {
        // 本来はSQLで絞り込むべきですが、今回は簡易的に全件取得してJavaでフィルタします
        return trainingDetailRepository.findAll().stream()
                .filter(d -> d.getMemo().getTraining().getUser().getUserId().equals(userId))
                .sorted((d1, d2) -> d2.getMemo().getTraining().getTrainingDay()
                        .compareTo(d1.getMemo().getTraining().getTrainingDay())) // 日付の新しい順
                .limit(5) // 最新5件だけ
                .toList();
    }
}