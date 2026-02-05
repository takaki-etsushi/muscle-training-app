package com.example.muscle_training_app.controller;

import com.example.muscle_training_app.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TrainingService trainingService;

    @GetMapping("/")
    public String showHome(Model model, Principal principal) {
        String userId = principal.getName();
        LocalDate today = LocalDate.now();

        // 今日の総負荷量
        model.addAttribute("totalLoad", trainingService.getDailyTotalLoad(userId, today));

        // 最近の履歴
        model.addAttribute("recentHistory", trainingService.getRecentHistory(userId));

        // ★復活 & 修正: 引数を3つ指定 ("week", null) してエラーを回避
        Map<String, List<?>> chartData = trainingService.getChartData(userId, "week", null);
        model.addAttribute("chartLabels", chartData.get("labels"));
        model.addAttribute("chartValues", chartData.get("values"));

        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}