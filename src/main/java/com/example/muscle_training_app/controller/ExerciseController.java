package com.example.muscle_training_app.controller;

import com.example.muscle_training_app.entity.BodyPart;
import com.example.muscle_training_app.entity.Exercise;
import com.example.muscle_training_app.repository.BodyPartRepository;
import com.example.muscle_training_app.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final BodyPartRepository bodyPartRepository;

    // 種目追加画面を表示
    @GetMapping("/exercises/add")
    public String showAddForm(Model model) {
        List<BodyPart> bodyParts = bodyPartRepository.findAll();
        model.addAttribute("bodyParts", bodyParts);
        return "exercise_form";
    }

    // 種目を保存する処理
    @PostMapping("/exercises/add")
    // ★修正: 型エラー回避のため String bodyPartId で受け取る
    public String addExercise(@RequestParam String bodyPartId, @RequestParam String exerciseName) {
        // ★修正: String型のIDで検索を行う
        BodyPart part = bodyPartRepository.findById(bodyPartId)
                .orElseThrow(() -> new IllegalArgumentException("無効な部位IDです: " + bodyPartId));

        Exercise newExercise = new Exercise();
        newExercise.setExerciseName(exerciseName); // フィールド名に合わせる
        newExercise.setBodyPart(part);

        exerciseRepository.save(newExercise);

        // ★修正: 登録後は「トレーニング記録画面」に直接戻る
        return "redirect:/training/record";
    }
}