package com.example.muscle_training_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "exercises")
@Data
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動採番設定
    private Long exerciseId; // 種目ID (数値型に変更)

    private String exerciseName; // 種目名

    // 部位テーブルとの関係 (多対1: 種目は1つの部位に属する)
    @ManyToOne
    @JoinColumn(name = "body_part_id")
    private BodyPart bodyPart;
}