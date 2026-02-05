package com.example.muscle_training_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "body_parts")
@Data
public class BodyPart {

    @Id
    private String bodyPartId; // 部位ID (文字列型)

    private String bodyPartName; // 部位名 (文字列型)
}