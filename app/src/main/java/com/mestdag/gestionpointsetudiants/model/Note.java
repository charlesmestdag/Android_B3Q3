package com.mestdag.gestionpointsetudiants.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table", 
        primaryKeys = {"evaluationId", "studentId"},
        indices = {@androidx.room.Index("evaluationId"), @androidx.room.Index("studentId")})
public class Note {
    private long evaluationId;
    private long studentId;
    private double note;

    public long getEvaluationId() { return evaluationId; }
    public void setEvaluationId(long evaluationId) { this.evaluationId = evaluationId; }
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public double getNote() { return note; }
    public void setNote(double note) { this.note = note; }
}