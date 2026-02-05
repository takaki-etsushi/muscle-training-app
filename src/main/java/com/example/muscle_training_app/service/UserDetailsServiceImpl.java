package com.example.muscle_training_app.service;

import com.example.muscle_training_app.entity.Users;
import com.example.muscle_training_app.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. データベースからユーザーIDで検索
        Users user = usersRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));

        // 2. Spring Securityが理解できる形式（Userオブジェクト）に変換して返す
        // ※今回は権限(Role)を全員 "USER" に設定しています
        return User.withUsername(user.getUserId())
                .password(user.getUserPass())
                .authorities(Collections.emptyList())
                .build();
    }
}