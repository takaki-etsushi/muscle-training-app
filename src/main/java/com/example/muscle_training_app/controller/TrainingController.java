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
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/training")
@RequiredArgsConstructor
public class TrainingController {

    private final ExerciseRepository exerciseRepository;
    private final BodyPartRepository bodyPartRepository;
    private final TrainingService trainingService;
    private final TrainingDetailRepository trainingDetailRepository;

    // (recordメソッド等は変更なし)
    @GetMapping("/record")
    public String showRecordForm(@RequestParam(required = false) LocalDate date, Model model) {
        List<BodyPart> bodyParts = bodyPartRepository.findAll();
        model.addAttribute("bodyParts", bodyParts);
        List<Exercise> exercises = exerciseRepository.findAll();
        model.addAttribute("exercises", exercises);
        TrainingForm form = new TrainingForm();
        form.setTrainingDate(date != null ? date : LocalDate.now());
        model.addAttribute("trainingForm", form);
        return "training_record";
    }

    @PostMapping("/record")
    public String recordTraining(@ModelAttribute TrainingForm form, Principal principal) {
        trainingService.recordTraining(form, principal.getName());
        return "redirect:/training/record?date=" + form.getTrainingDate();
    }

    // ★修正：履歴画面（検索とグラフ設定を受け取る）
    @GetMapping("/history")
    public String showHistory(
            @RequestParam(required = false, defaultValue = "week") String period,
            @RequestParam(required = false) Long graphExerciseId,
            @RequestParam(required = false) Long listExerciseId,
            @RequestParam(required = false, defaultValue = "list") String viewMode, // ★追加
            Model model,
            Principal principal) {

        String userId = principal.getName();

        // (データ取得処理はそのまま...)
        List<TrainingDetail> details = trainingService.getFilteredHistory(userId, listExerciseId);
        model.addAttribute("details", details);

        Map<String, List<?>> chartData = trainingService.getChartData(userId, period, graphExerciseId);
        model.addAttribute("chartLabels", chartData.get("labels"));
        model.addAttribute("chartValues", chartData.get("values"));

        model.addAttribute("exercises", exerciseRepository.findAll());

        model.addAttribute("currentPeriod", period);
        model.addAttribute("currentGraphExerciseId", graphExerciseId);
        model.addAttribute("currentListExerciseId", listExerciseId);

        // ★追加: ビューモードを画面に渡す
        model.addAttribute("viewMode", viewMode);

        return "history";
    }

    // TrainingController.java に以下を追加

    // 削除処理
    @PostMapping("/history/delete/{id}")
    public String deleteHistory(@PathVariable Long id, Principal principal) {
        trainingService.deleteTrainingDetail(id, principal.getName());
        return "redirect:/training/history";
    }

    // 編集画面の表示
    @GetMapping("/history/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        TrainingDetail detail = trainingService.getTrainingDetail(id);

        // フォームに既存データを詰める
        TrainingForm form = new TrainingForm();
        form.setTrainingDate(detail.getMemo().getTraining().getTrainingDay());
        form.setExerciseId(detail.getMemo().getExercise().getExerciseId());
        form.setWeight(detail.getWeight());
        form.setRep(detail.getRep());
        form.setSetCount(detail.getSetCount());
        form.setMemo(detail.getMemo().getMemoContent());

        model.addAttribute("trainingForm", form);
        model.addAttribute("detailId", id); // 更新時にIDが必要

        // ドロップダウン用データ
        model.addAttribute("bodyParts", bodyPartRepository.findAll());
        model.addAttribute("exercises", exerciseRepository.findAll());

        return "training_edit";
    }

    // 更新処理
    @PostMapping("/history/update")
    public String updateHistory(@RequestParam Long detailId, @ModelAttribute TrainingForm form, Principal principal) {
        trainingService.updateTrainingDetail(detailId, form, principal.getName());
        return "redirect:/training/history";
    }
}