package com.mestdag.gestionpointsetudiants.utils;

import com.mestdag.gestionpointsetudiants.model.Evaluation;
import com.mestdag.gestionpointsetudiants.model.Note;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.ArrayList;
import com.mestdag.gestionpointsetudiants.model.Student;

public class EvaluationCalculator {
    
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
     * Calcule les points obtenus par un étudiant pour une évaluation donnée
     */
    public static double calculateStudentPointsForEvaluation(long studentId, Evaluation evaluation, List<Note> notes) {
        if (evaluation.getParentId() == 0) {
            // Évaluation principale
            return calculateStudentPointsForMainEvaluation(studentId, evaluation, notes);
        } else {
            // Sous-évaluation
            return calculateStudentPointsForSubEvaluation(studentId, evaluation, notes);
        }
    }
    
    /**
     * Calcule les points obtenus par un étudiant pour une évaluation principale (récursif)
     */
    private static double calculateStudentPointsForMainEvaluation(long studentId, Evaluation evaluation, List<Note> notes) {
        // Obtenir les sous-évaluations de cette évaluation principale
        List<Evaluation> allEvaluations = getEvaluationsFromNotes(notes);
        List<Evaluation> subEvaluations = evaluation.getSubEvaluations(allEvaluations);
        
        if (subEvaluations.isEmpty()) {
            // Pas de sous-évaluations, calculer directement sur l'évaluation principale
            return notes.stream()
                    .filter(note -> note.getEvaluationId() == evaluation.getId() && note.getStudentId() == studentId)
                    .mapToDouble(Note::getNote)
                    .sum();
        } else {
            // Calculer la somme des points des sous-évaluations
            return subEvaluations.stream()
                    .mapToDouble(subEval -> calculateStudentPointsForEvaluation(studentId, subEval, notes))
                    .sum();
        }
    }
    
    /**
     * Calcule les points obtenus par un étudiant pour une sous-évaluation
     */
    private static double calculateStudentPointsForSubEvaluation(long studentId, Evaluation evaluation, List<Note> notes) {
        return notes.stream()
                .filter(note -> note.getEvaluationId() == evaluation.getId() && note.getStudentId() == studentId)
                .mapToDouble(Note::getNote)
                .sum();
    }
    
    /**
     * Calcule le pourcentage de réussite pour un étudiant dans un cours
     */
    public static double calculateStudentSuccessRate(long studentId, long courseId, List<Evaluation> evaluations, List<Note> notes) {
        List<Evaluation> courseEvaluations = evaluations.stream()
                .filter(eval -> eval.getCourseId() == courseId)
                .collect(Collectors.toList());
        
        double totalPointsObtained = courseEvaluations.stream()
                .mapToDouble(eval -> calculateStudentPointsForEvaluation(studentId, eval, notes))
                .sum();
        
        double totalPointsPossible = courseEvaluations.stream()
                .mapToDouble(eval -> eval.calculateTotalPointsRecursive(evaluations))
                .sum();
        
        return totalPointsPossible > 0 ? (totalPointsObtained / totalPointsPossible) * 100.0 : 0.0;
    }
    
    /**
     * Calcule les statistiques globales pour un cours
     */
    public static Map<String, Double> calculateCourseStatistics(long courseId, List<Evaluation> evaluations, List<Note> notes) {
        Map<String, Double> statistics = new HashMap<>();
        
        List<Evaluation> courseEvaluations = evaluations.stream()
                .filter(eval -> eval.getCourseId() == courseId)
                .collect(Collectors.toList());
        
        // Total des points possibles
        double totalPointsPossible = courseEvaluations.stream()
                .mapToDouble(eval -> eval.calculateTotalPointsRecursive(evaluations))
                .sum();
        
        // Total des points obtenus par tous les étudiants
        double totalPointsObtained = notes.stream()
                .filter(note -> courseEvaluations.stream().anyMatch(eval -> eval.getId() == note.getEvaluationId()))
                .mapToDouble(Note::getNote)
                .sum();
        
        // Nombre d'étudiants
        long studentCount = notes.stream()
                .filter(note -> courseEvaluations.stream().anyMatch(eval -> eval.getId() == note.getEvaluationId()))
                .map(Note::getStudentId)
                .distinct()
                .count();
        
        statistics.put("totalPointsPossible", totalPointsPossible);
        statistics.put("totalPointsObtained", totalPointsObtained);
        statistics.put("averagePoints", studentCount > 0 ? totalPointsObtained / studentCount : 0.0);
        statistics.put("successRate", totalPointsPossible > 0 ? (totalPointsObtained / totalPointsPossible) * 100.0 : 0.0);
        statistics.put("studentCount", (double) studentCount);
        
        return statistics;
    }
    
    /**
     * Vérifie si une évaluation est complète (tous les étudiants ont une note)
     */
    public static boolean isEvaluationComplete(Evaluation evaluation, List<Student> students, List<Note> notes) {
        long expectedNotes = students.size();
        long actualNotes = notes.stream()
                .filter(note -> note.getEvaluationId() == evaluation.getId())
                .count();
        
        return actualNotes >= expectedNotes;
    }
    
    /**
     * Calcule la moyenne d'une évaluation
     */
    public static double calculateEvaluationAverage(Evaluation evaluation, List<Note> notes) {
        List<Note> evaluationNotes = notes.stream()
                .filter(note -> note.getEvaluationId() == evaluation.getId())
                .collect(Collectors.toList());
        
        if (evaluationNotes.isEmpty()) {
            return 0.0;
        }
        
        return evaluationNotes.stream()
                .mapToDouble(Note::getNote)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Extrait les évaluations à partir des notes
     */
    private static List<Evaluation> getEvaluationsFromNotes(List<Note> notes) {
        // Cette méthode devrait être remplacée par un appel à la base de données
        // Pour l'instant, on retourne une liste vide
        return new ArrayList<>();
    }
    
    /**
     * Valide la cohérence des points dans une hiérarchie d'évaluations
     */
    public static boolean validateEvaluationHierarchy(List<Evaluation> evaluations) {
        for (Evaluation eval : evaluations) {
            if (eval.getParentId() == 0) {
                // Évaluation principale
                List<Evaluation> subEvaluations = eval.getSubEvaluations(evaluations);
                
                if (!subEvaluations.isEmpty()) {
                    double subTotal = subEvaluations.stream()
                            .mapToDouble(subEval -> subEval.calculateTotalPoints())
                            .sum();
                    
                    // Vérifier que la somme des sous-évaluations ne dépasse pas 20
                    if (subTotal > 20.0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
