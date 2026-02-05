package com.example.muscle_training_app.controller;

import com.example.muscle_training_app.dto.TrainingForm;
import com.example.muscle_training_app.entity.Exercise;
import com.example.muscle_training_app.entity.TrainingDetail;
import com.example.muscle_training_app.repository.ExerciseRepository;
import com.example.muscle_training_app.repository.TrainingDetailRepository; // ★追加
import com.example.muscle_training_app.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/training")
@RequiredArgsConstructor
public class TrainingController {

    private final ExerciseRepository exerciseRepository;
    private final TrainingService trainingService;
    private final TrainingDetailRepository trainingDetailRepository; // ★追加

    // 記録画面を表示
    @GetMapping("/record")
    public String showRecordForm(Model model) {
        List<Exercise> exercises = exerciseRepository.findAll();
        model.addAttribute("exercises", exercises);
        model.addAttribute("trainingForm", new TrainingForm());
        return "training_record";
    }

    // ★変更：記録ボタンが押された時の処理
    @PostMapping("/record")
    public String recordTraining(@ModelAttribute TrainingForm form, java.security.Principal principal) {

        // ★変更：ログイン中のユーザーID (principal.getName()) を渡す
        trainingService.recordTraining(form, principal.getName());

        return "redirect:/";
    }

    // ★追加：履歴一覧を表示
    @GetMapping("/history")
    public String showHistory(Model model) {
        // 全ての詳細データ（重量や回数）を取得
        List<TrainingDetail> details = trainingDetailRepository.findAll();

        // 画面に渡す
        model.addAttribute("details", details);

        return "history"; // history.html を表示
    }
}