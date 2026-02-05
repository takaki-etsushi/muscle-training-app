package com.example.muscle_training_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "memo")
@Data
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memoId; // メモID (数値・自動採番)

    // トレーニングテーブルとの関係 (多対1)
    @ManyToOne
    @JoinColumn(name = "training_id")
    private Training training;

    // 種目テーブルとの関係 (多対1)
    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private String memoContent; // メモ内容
}