package com.example.muscle_training_app.config;

import com.example.muscle_training_app.entity.BodyPart;
import com.example.muscle_training_app.entity.Exercise;
import com.example.muscle_training_app.entity.Users;
import com.example.muscle_training_app.repository.BodyPartRepository;
import com.example.muscle_training_app.repository.ExerciseRepository;
import com.example.muscle_training_app.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final BodyPartRepository bodyPartRepository;
    private final ExerciseRepository exerciseRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // データが既に存在する場合は何もしない
//        if (bodyPartRepository.count() > 0) {
//            return;
//        }

        System.out.println("--- 初期データの投入を開始します ---");

        // 1. テストユーザー作成
        Users user = new Users();
        user.setUserId("takaki");
        user.setUserPass(passwordEncoder.encode("password"));
        usersRepository.save(user);

        // 2. 部位データの作成
        createBodyPart("chest", "胸");
        createBodyPart("back", "背中");
        createBodyPart("legs", "脚");
        createBodyPart("shoulder", "肩");
        createBodyPart("arm", "腕");
        createBodyPart("glutes", "お尻");
        createBodyPart("abs", "腹筋");
        createBodyPart("other", "その他");

        // 3. 種目データの作成
        // 胸
        createExercises("chest", Arrays.asList(
                "ベンチプレス", "インクラインベンチプレス", "デクラインベンチプレス",
                "ダンベルフライ", "インクラインダンベルフライ", "ケーブルクロスオーバー",
                "チェストプレス", "ディップス", "プッシュアップ", "ペックデックフライ"
        ));

        // 背中
        createExercises("back", Arrays.asList(
                "デッドリフト", "ベントオーバーロー", "ワンハンドダンベルロー",
                "ラットプルダウン", "チンニング（懸垂）", "シーテッドロー",
                "Tバーロー", "インクラインダンベルロー", "ケーブルプルオーバー", "バックエクステンション"
        ));

        // 脚
        createExercises("legs", Arrays.asList(
                "スクワット", "バーベルスクワット", "レッグプレス", "ランジ",
                "ブルガリアンスクワット", "レッグエクステンション", "レッグカール",
                "デッドリフト（脚狙い）", "カーフレイズ", "ヒップスラスト"
        ));

        // 肩
        createExercises("shoulder", Arrays.asList(
                "ショルダープレス", "サイドレイズ", "フロントレイズ", "リアレイズ",
                "アップライトロー", "アーノルドプレス", "フェイスプル",
                "ダンベルショルダープレス", "ケーブルサイドレイズ", "オーバーヘッドプレス"
        ));

        // 腕
        createExercises("arm", Arrays.asList(
                "バーベルカール", "EZバーカール", "ダンベルカール", "ハンマーカール",
                "プリーチャーカール", "インクラインダンベルカール",
                "トライセプスプレスダウン", "スカルクラッシャー", "ナローベンチプレス", "キックバック"
        ));

        // お尻
        createExercises("glutes", Arrays.asList(
                "ヒップスラスト", "ヒップアブダクション", "ルーマニアンデッドリフト"
        ));

        // 腹筋とその他はユーザー追加用として、とりあえず代表的なものを1つだけ入れておくか、空にしておく
        createExercises("abs", Arrays.asList("クランチ", "レッグレイズ", "アブローラー"));

        System.out.println("--- 初期データの投入が完了しました ---");
    }

    private void createBodyPart(String id, String name) {
        BodyPart part = new BodyPart();
        part.setBodyPartId(id);
        part.setBodyPartName(name);
        bodyPartRepository.save(part);
    }

    private void createExercises(String bodyPartId, List<String> names) {
        BodyPart part = bodyPartRepository.findById(bodyPartId).orElseThrow();
        for (String name : names) {
            Exercise ex = new Exercise();
            ex.setExerciseName(name);
            ex.setBodyPart(part);
            exerciseRepository.save(ex);
        }
    }
}