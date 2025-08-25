package com.mestdag.gestionpointsetudiants.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.room.ColumnInfo;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Entity(tableName = "evaluation_table", 
        indices = {@androidx.room.Index("parent_id"), @androidx.room.Index("course_id")})
public class Evaluation {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    @ColumnInfo(name = "parent_id")
    private long parentId; // 0 si pas de parent (évaluation racine)
    
    @ColumnInfo(name = "course_id")
    private long courseId; // Le cours auquel appartient cette évaluation
    
    @ColumnInfo(name = "name")
    private String name;
    
    @ColumnInfo(name = "points_max")
    private double pointsMax;
    
    @ColumnInfo(name = "evaluation_type")
    private String evaluationType; // Pour Room, stocker le type concret

    // Constructeur par défaut pour Room
    public Evaluation() {
        this.evaluationType = "Evaluation";
    }

    // Méthodes polymorphiques basées sur le parentId
    @Ignore
    public double calculateTotalPoints() {
        if (isMainEvaluation()) {
            // Évaluation principale : toujours 20 points
            return 20.0;
        } else {
            // Sous-évaluation : points personnalisés
            return getPointsMax();
        }
    }
    
    @Ignore
    public String getType() {
        return isMainEvaluation() ? "MAIN" : "SUB";
    }
    
    @Ignore
    public boolean canHaveSubEvaluations() {
        // Seules les évaluations principales peuvent avoir des sous-évaluations
        return isMainEvaluation();
    }
    
    @Ignore
    public String getDisplayName() {
        if (isMainEvaluation()) {
            return getName();
        } else {
            return "  ↳ " + getName(); // Indentation pour les sous-évaluations
        }
    }
    
    @Ignore
    public double calculateTotalPointsRecursive(List<Evaluation> allEvaluations) {
        if (isMainEvaluation()) {
            // Évaluation principale : calculer la somme des sous-évaluations
            List<Evaluation> subEvaluations = getSubEvaluations(allEvaluations);
            if (subEvaluations.isEmpty()) {
                return calculateTotalPoints();
            } else {
                return subEvaluations.stream()
                        .mapToDouble(eval -> eval.calculateTotalPointsRecursive(allEvaluations))
                        .sum();
            }
        } else {
            // Sous-évaluation : retourner ses propres points
            return calculateTotalPoints();
        }
    }
    
    @Ignore
    public List<Evaluation> getSubEvaluations(List<Evaluation> allEvaluations) {
        if (isMainEvaluation()) {
            // Retourner toutes les évaluations qui ont cette évaluation comme parent
            return allEvaluations.stream()
                    .filter(eval -> eval.getParentId() == getId())
                    .collect(Collectors.toList());
        } else {
            // Les sous-évaluations n'ont pas de sous-évaluations
            return new ArrayList<>();
        }
    }
    
    // Méthodes utilitaires pour Room
    @Ignore
    public boolean isMainEvaluation() {
        return getParentId() == 0;
    }
    
    @Ignore
    public boolean isSubEvaluation() {
        return getParentId() != 0;
    }

    // Getters et setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getParentId() { return parentId; }
    public void setParentId(long parentId) { this.parentId = parentId; }
    public long getCourseId() { return courseId; }
    public void setCourseId(long courseId) { this.courseId = courseId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPointsMax() { return pointsMax; }
    public void setPointsMax(double pointsMax) { this.pointsMax = pointsMax; }
    public String getEvaluationType() { return evaluationType; }
    public void setEvaluationType(String evaluationType) { this.evaluationType = evaluationType; }

    // Méthode utilitaire pour créer une évaluation principale
    @Ignore
    public static Evaluation createMainEvaluation(String name, long courseId) {
        Evaluation evaluation = new Evaluation();
        evaluation.setName(name);
        evaluation.setCourseId(courseId);
        evaluation.setParentId(0); // Pas de parent
        evaluation.setPointsMax(20.0); // Toujours 20 pour les évaluations principales
        evaluation.setEvaluationType("MainEvaluation");
        return evaluation;
    }
    
    // Méthode utilitaire pour créer une sous-évaluation
    @Ignore
    public static Evaluation createSubEvaluation(String name, double pointsMax, long parentId, long courseId) {
        Evaluation evaluation = new Evaluation();
        evaluation.setName(name);
        evaluation.setPointsMax(pointsMax);
        evaluation.setParentId(parentId);
        evaluation.setCourseId(courseId);
        evaluation.setEvaluationType("SubEvaluation");
        return evaluation;
    }
}