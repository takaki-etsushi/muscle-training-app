package com.example.muscle_training_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "training_detail")
@Data
public class TrainingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId; // 詳細ID (数値・自動採番)

    // メモテーブルとの関係 (多対1)
    @ManyToOne
    @JoinColumn(name = "memo_id")
    private Memo memo;

    private Integer weight; // 重量

    private Integer rep;    // レップ数 (回数)

    @Column(name = "set_count") // データベース上での名前を指定
    private Integer setCount;   // セット数 (変数名を変更)
}