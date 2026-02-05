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

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final BodyPartRepository bodyPartRepository;
    private final ExerciseRepository exerciseRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder; // ★追加：暗号化用

    @Override
    public void run(String... args) throws Exception {
        // データが既に存在する場合は何もしない
        if (bodyPartRepository.count() > 0) {
            return;
        }

        System.out.println("--- 初期データの投入を開始します ---");

        // ★追加：テストユーザーの作成（暗号化パスワード）
        Users user = new Users();
        user.setUserId("takaki"); // IDを "takaki" にしましょう
        user.setUserPass(passwordEncoder.encode("password")); // パスワード "password" を暗号化して保存
        usersRepository.save(user);
        System.out.println("--- テストユーザー(ID: takaki / PASS: password)を作成しました ---");

        // 1. 部位データの作成
        BodyPart chest = new BodyPart();
        chest.setBodyPartId("chest");
        chest.setBodyPartName("胸");
        bodyPartRepository.save(chest);

        BodyPart back = new BodyPart();
        back.setBodyPartId("back");
        back.setBodyPartName("背中");
        bodyPartRepository.save(back);

        BodyPart legs = new BodyPart();
        legs.setBodyPartId("legs");
        legs.setBodyPartName("脚");
        bodyPartRepository.save(legs);

        // 2. 種目データの作成
        Exercise benchPress = new Exercise();
        benchPress.setExerciseName("ベンチプレス");
        benchPress.setBodyPart(chest);
        exerciseRepository.save(benchPress);

        Exercise deadlift = new Exercise();
        deadlift.setExerciseName("デッドリフト");
        deadlift.setBodyPart(back);
        exerciseRepository.save(deadlift);

        Exercise squat = new Exercise();
        squat.setExerciseName("スクワット");
        squat.setBodyPart(legs);
        exerciseRepository.save(squat);

        System.out.println("--- 初期データの投入が完了しました ---");
    }
}