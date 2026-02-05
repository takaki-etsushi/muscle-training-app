package com.example.muscle_training_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users") // データベース上のテーブル名を指定
@Data // Lombok: ゲッター・セッターを自動生成
public class Users {

    @Id
    private String userId; // 利用者ID (文字列型)

    private String userPass; // パスワード (文字列型)
}