package com.example.muscle_training_app.controller;

import com.example.muscle_training_app.dto.TrainingForm;
import com.example.muscle_training_app.entity.BodyPart; // 追加
import com.example.muscle_training_app.entity.Exercise;
import com.example.muscle_training_app.entity.TrainingDetail;
import com.example.muscle_training_app.repository.BodyPartRepository; // 追加
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
import org.springframework.web.bind.annotation.RequestParam; // 追加

import java.security.Principal;
import java.time.LocalDate; // 追加
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/training")
@RequiredArgsConstructor
public class TrainingController {

    private final ExerciseRepository exerciseRepository;
    private final BodyPartRepository bodyPartRepository; // 追加
    private final TrainingService trainingService;
    private final TrainingDetailRepository trainingDetailRepository;

    // ★修正: 日付パラメータを受け取れるように変更
    @GetMapping("/record")
    public String showRecordForm(@RequestParam(required = false) LocalDate date, Model model) {

        // 1. 部位リストを取得（絞り込み用）
        List<BodyPart> bodyParts = bodyPartRepository.findAll();
        model.addAttribute("bodyParts", bodyParts);

        // 2. 全種目を取得（JSでフィルタリングするため全件渡す）
        List<Exercise> exercises = exerciseRepository.findAll();
        model.addAttribute("exercises", exercises);

        // 3. フォームの初期化（日付指定があればセット、なければ今日）
        TrainingForm form = new TrainingForm();
        form.setTrainingDate(date != null ? date : LocalDate.now());

        model.addAttribute("trainingForm", form);
        return "training_record";
    }

    // ★修正: 記録後に日付を持ってリダイレクト
    @PostMapping("/record")
    public String recordTraining(@ModelAttribute TrainingForm form, Principal principal) {
        trainingService.recordTraining(form, principal.getName());

        // 日付をクエリパラメータとして付与してリダイレクト（例: /training/record?date=2026-02-06）
        return "redirect:/training/record?date=" + form.getTrainingDate();
    }

    @GetMapping("/history")
    public String showHistory(Model model, Principal principal) {
        List<TrainingDetail> details = trainingDetailRepository.findAll();
        model.addAttribute("details", details);

        String userId = principal.getName();
        Map<String, List<?>> chartData = trainingService.getChartData(userId);
        model.addAttribute("chartLabels", chartData.get("labels"));
        model.addAttribute("chartValues", chartData.get("values"));

        return "history";
    }
}