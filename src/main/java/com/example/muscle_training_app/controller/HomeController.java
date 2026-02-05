package com.example.muscle_training_app.controller;

import com.example.muscle_training_app.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TrainingService trainingService;

    @GetMapping("/")
    public String showHome(Model model, Principal principal) {
        // ログインしているユーザーのIDを取得
        String userId = principal.getName();

        // 今日の日付を取得
        LocalDate today = LocalDate.now();

        // 1. 今日の総負荷量を取得
        Long totalLoad = trainingService.getDailyTotalLoad(userId, today);
        model.addAttribute("totalLoad", totalLoad);

        // 2. 最近の履歴を取得（カレンダーエリアに表示するため）
        model.addAttribute("recentHistory", trainingService.getRecentHistory(userId));

        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}