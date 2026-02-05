package com.example.muscle_training_app.controller;

import com.example.muscle_training_app.entity.Users;
import com.example.muscle_training_app.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    // 登録画面を表示
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    // 登録ボタンが押された時の処理
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        Users newUser = new Users();
        newUser.setUserId(username);
        // パスワードを暗号化してセット
        newUser.setUserPass(passwordEncoder.encode(password));

        usersRepository.save(newUser);

        // 登録が終わったらログイン画面へ飛ばす
        return "redirect:/login";
    }
}