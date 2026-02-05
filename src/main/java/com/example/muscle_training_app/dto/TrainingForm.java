package com.example.muscle_training_app.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class TrainingForm {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate; // 日付

    private Long exerciseId; // 選ばれた種目のID

    private Integer weight; // 重量
    private Integer rep;    // 回数
    private Integer setCount; // セット数

    private String memo; // メモ
}