package com.example.muscle_training_app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "training")
@Data
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainingId; // トレーニングID (数値・自動採番)

    // 利用者テーブルとの関係 (多対1)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private LocalDate trainingDay; // トレーニング日 (日付型)
}