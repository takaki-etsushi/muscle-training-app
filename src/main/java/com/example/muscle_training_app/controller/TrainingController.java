package com.example.muscle_training_app.controller;

import com.example.muscle_training_app.dto.TrainingForm;
import com.example.muscle_training_app.entity.BodyPart;
import com.example.muscle_training_app.entity.Exercise;
import com.example.muscle_training_app.entity.TrainingDetail;
import com.example.muscle_training_app.repository.BodyPartRepository;
import com.example.muscle_training_app.repository.ExerciseRepository;
import com.example.muscle_training_app.repository.TrainingDetailRepository;
import com.example.muscle_training_app.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal; // ★追加
import java.util.List;
import java.util.Map; // ★追加

@Controller
@RequestMapping("/training")
@RequiredArgsConstructor
public class TrainingController {

    private final ExerciseRepository exerciseRepository;
    private final TrainingService trainingService;
    private final TrainingDetailRepository trainingDetailRepository;

    @GetMapping("/record")
    public String showRecordForm(Model model) {
        List<Exercise> exercises = exerciseRepository.findAll();
        model.addAttribute("exercises", exercises);
        model.addAttribute("trainingForm", new TrainingForm());
        return "training_record";
    }

    @PostMapping("/record")
    public String recordTraining(@ModelAttribute TrainingForm form, Principal principal) {
        trainingService.recordTraining(form, principal.getName());
        return "redirect:/";
    }

    // ★修正：履歴画面表示処理
    @GetMapping("/history")
    public String showHistory(Model model, Principal principal) {
        // 1. 一覧データの取得（既存の処理）
        List<TrainingDetail> details = trainingDetailRepository.findAll();
        model.addAttribute("details", details);

        // 2. ★追加：グラフ用データの取得（TrainingServiceを利用）
        String userId = principal.getName();
        Map<String, List<?>> chartData = trainingService.getChartData(userId);
        model.addAttribute("chartLabels", chartData.get("labels"));
        model.addAttribute("chartValues", chartData.get("values"));

        return "history";
    }
}