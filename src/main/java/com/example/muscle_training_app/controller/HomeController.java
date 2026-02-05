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

        model.addAttribute("totalLoad", trainingService.getDailyTotalLoad(userId, today));
        model.addAttribute("recentHistory", trainingService.getRecentHistory(userId));

        // ★追加：グラフ用データの取得とModelへの追加
        Map<String, List<?>> chartData = trainingService.getChartData(userId);
        model.addAttribute("chartLabels", chartData.get("labels"));
        model.addAttribute("chartValues", chartData.get("values"));

        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}