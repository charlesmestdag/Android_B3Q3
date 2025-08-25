package com.mestdag.gestionpointsetudiants.utils;

import com.mestdag.gestionpointsetudiants.model.Evaluation;
import java.util.List;
import java.util.ArrayList;

public class EvaluationFactory {
    
    /**
     * Crée une évaluation principale
     */
    public static Evaluation createMainEvaluation(String name, long courseId) {
        return Evaluation.createMainEvaluation(name, courseId);
    }
    
    /**
     * Crée une sous-évaluation
     */
    public static Evaluation createSubEvaluation(String name, double pointsMax, long parentId, long courseId) {
        return Evaluation.createSubEvaluation(name, pointsMax, parentId, courseId);
    }
    
    /**
     * Convertit une évaluation générique en évaluation typée basée sur son type
     */
    public static Evaluation convertToTypedEvaluation(Evaluation evaluation) {
        if (evaluation == null) return null;
        
        // Les évaluations sont maintenant polymorphiques par défaut
        // Pas besoin de conversion car toutes les méthodes polymorphiques sont dans la classe de base
        return evaluation;
    }
    
    /**
     * Convertit une liste d'évaluations génériques en évaluations typées
     */
    public static List<Evaluation> convertToTypedEvaluations(List<Evaluation> evaluations) {
        List<Evaluation> typedEvaluations = new ArrayList<>();
        
        for (Evaluation eval : evaluations) {
            typedEvaluations.add(convertToTypedEvaluation(eval));
        }
        
        return typedEvaluations;
    }
    
    /**
     * Organise les évaluations en hiérarchie (évaluations principales avec leurs sous-évaluations)
     */
    public static List<Evaluation> organizeEvaluationsHierarchically(List<Evaluation> allEvaluations) {
        List<Evaluation> mainEvaluations = new ArrayList<>();
        
        // Trouver toutes les évaluations principales
        for (Evaluation eval : allEvaluations) {
            if (eval.isMainEvaluation()) {
                mainEvaluations.add(eval);
            }
        }
        
        return mainEvaluations;
    }
    
    /**
     * Calcule le total des points pour un cours donné
     */
    public static double calculateCourseTotalPoints(List<Evaluation> evaluations, long courseId) {
        return evaluations.stream()
                .filter(eval -> eval.getCourseId() == courseId)
                .mapToDouble(eval -> eval.calculateTotalPointsRecursive(evaluations))
                .sum();
    }
    
    /**
     * Vérifie si une évaluation peut être convertie en sous-évaluation
     */
    public static boolean canBeSubEvaluation(Evaluation evaluation, List<Evaluation> allEvaluations) {
        // Vérifier que l'évaluation parente existe et est une évaluation principale
        if (evaluation.getParentId() == 0) {
            return false; // Pas de parent
        }
        
        Evaluation parent = allEvaluations.stream()
                .filter(eval -> eval.getId() == evaluation.getParentId())
                .findFirst()
                .orElse(null);
        
        return parent != null && parent.canHaveSubEvaluations();
    }
    
    /**
     * Valide une évaluation avant sauvegarde
     */
    public static boolean validateEvaluation(Evaluation evaluation, List<Evaluation> allEvaluations) {
        if (evaluation.getName() == null || evaluation.getName().trim().isEmpty()) {
            return false;
        }
        
        if (evaluation.getPointsMax() <= 0) {
            return false;
        }
        
        if (evaluation.isSubEvaluation()) {
            // C'est une sous-évaluation, vérifier que le parent existe
            boolean parentExists = allEvaluations.stream()
                    .anyMatch(eval -> eval.getId() == evaluation.getParentId());
            
            if (!parentExists) {
                return false;
            }
        }
        
        return true;
    }
}
