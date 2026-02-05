package com.example.muscle_training_app.controller;

import com.example.muscle_training_app.entity.Users;
import com.example.muscle_training_app.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 追加
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model) {
        // ★追加: バリデーションチェック
        if (password.length() < 5 || !password.matches("^[a-zA-Z0-9]+$")) {
            model.addAttribute("error", "パスワードは5文字以上の英数字で入力してください");
            return "register";
        }

        // ユーザーID重複チェックも入れると親切
        if (usersRepository.existsById(username)) {
            model.addAttribute("error", "そのユーザーIDは既に使用されています");
            return "register";
        }

        Users newUser = new Users();
        newUser.setUserId(username);
        newUser.setUserPass(passwordEncoder.encode(password));

        usersRepository.save(newUser);

        return "redirect:/login?registered";
    }
}