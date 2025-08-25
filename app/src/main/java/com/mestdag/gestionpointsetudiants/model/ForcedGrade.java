package com.mestdag.gestionpointsetudiants.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entité pour stocker les notes forcées par l'utilisateur
 * Permet de remplacer une note calculée par une note définie manuellement
 */
@Entity(tableName = "forced_grade_table")
public class ForcedGrade {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long evaluationId;
    private long studentId;
    private double forcedGrade;
    private String reason; // Raison du forçage (optionnel)
    private long timestamp; // Quand la note a été forcée

    public ForcedGrade() {}

    @androidx.room.Ignore
    public ForcedGrade(long evaluationId, long studentId, double forcedGrade, String reason) {
        this.evaluationId = evaluationId;
        this.studentId = studentId;
        this.forcedGrade = forcedGrade;
        this.reason = reason;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters et Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getEvaluationId() { return evaluationId; }
    public void setEvaluationId(long evaluationId) { this.evaluationId = evaluationId; }

    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }

    public double getForcedGrade() { return forcedGrade; }
    public void setForcedGrade(double forcedGrade) { this.forcedGrade = forcedGrade; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
