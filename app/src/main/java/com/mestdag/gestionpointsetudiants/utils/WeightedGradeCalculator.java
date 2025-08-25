package com.mestdag.gestionpointsetudiants.utils;

import com.mestdag.gestionpointsetudiants.model.Evaluation;
import com.mestdag.gestionpointsetudiants.model.Note;
import com.mestdag.gestionpointsetudiants.model.ForcedGrade;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe utilitaire pour calculer les moyennes pondérées des évaluations
 * en tenant compte des points maximum et des notes forcées
 */
public class WeightedGradeCalculator {

    /**
     * Calcule la moyenne pondérée d'un étudiant pour une évaluation donnée
     * @param evaluation L'évaluation
     * @param notes Les notes de l'étudiant pour cette évaluation et ses sous-évaluations
     * @param forcedGrade La note forcée éventuelle
     * @param allEvaluations Toutes les évaluations du cours (pour trouver les sous-évaluations)
     * @return La moyenne pondérée arrondie au 0.5 près
     */
    public static double calculateStudentAverage(Evaluation evaluation, List<Note> notes, ForcedGrade forcedGrade, List<Evaluation> allEvaluations) {
        // Si une note est forcée, la retourner directement
        if (forcedGrade != null) {
            return GradeUtils.roundToHalf(forcedGrade.getForcedGrade());
        }

        // Si c'est une évaluation principale (parentId = 0), calculer la moyenne des sous-évaluations
        if (evaluation.getParentId() == 0) {
            return calculateMainEvaluationAverage(evaluation, notes, allEvaluations);
        } else {
            // C'est une sous-évaluation, calculer la note simple
            return calculateSubEvaluationGrade(evaluation, notes);
        }
    }

    /**
     * Calcule la moyenne d'une évaluation principale (avec sous-évaluations)
     */
    private static double calculateMainEvaluationAverage(Evaluation mainEvaluation, List<Note> notes, List<Evaluation> allEvaluations) {
        if (notes == null || notes.isEmpty()) {
            return 0.0;
        }

        // Trouver toutes les sous-évaluations de cette évaluation principale
        List<Evaluation> subEvaluations = new ArrayList<>();
        for (Evaluation eval : allEvaluations) {
            if (eval.getParentId() == mainEvaluation.getId()) {
                subEvaluations.add(eval);
            }
        }

        // Si pas de sous-évaluations, chercher une note directe pour cette évaluation
        if (subEvaluations.isEmpty()) {
            for (Note note : notes) {
                if (note.getEvaluationId() == mainEvaluation.getId()) {
                    return GradeUtils.roundToHalf(note.getNote());
                }
            }
            return 0.0;
        }

        // Calculer la moyenne pondérée des sous-évaluations
        double totalWeightedSum = 0.0;
        double totalWeight = 0.0;

        for (Evaluation subEval : subEvaluations) {
            // Chercher la note pour cette sous-évaluation
            Note subNote = null;
            for (Note note : notes) {
                if (note.getEvaluationId() == subEval.getId()) {
                    subNote = note;
                    break;
                }
            }

            if (subNote != null) {
                // Calcul correct : (note / maxPoints) * weight
                double normalizedGrade = subNote.getNote() / subEval.getPointsMax();
                double weight = subEval.getPointsMax();
                totalWeightedSum += normalizedGrade * weight;
                totalWeight += weight;
            }
        }

        if (totalWeight == 0) {
            return 0.0;
        }

        // Calculer la moyenne pondérée et la ramener sur 20 points
        double average = totalWeightedSum / totalWeight;
        // Ramener sur 20 points : average * 20
        double averageOn20 = average * 20;
        return GradeUtils.roundToHalf(averageOn20);
    }

    /**
     * Calcule le total maximum d'une évaluation principale basé sur ses sous-évaluations
     */
    public static double calculateMainEvaluationMaxPoints(Evaluation mainEvaluation, List<Evaluation> allEvaluations) {
        // Trouver toutes les sous-évaluations de cette évaluation principale
        List<Evaluation> subEvaluations = new ArrayList<>();
        for (Evaluation eval : allEvaluations) {
            if (eval.getParentId() == mainEvaluation.getId()) {
                subEvaluations.add(eval);
            }
        }

        // Si pas de sous-évaluations, retourner les points maximum de l'évaluation principale
        if (subEvaluations.isEmpty()) {
            return mainEvaluation.getPointsMax();
        }

        // Calculer la somme des points maximum des sous-évaluations
        double totalMaxPoints = 0.0;
        for (Evaluation subEval : subEvaluations) {
            totalMaxPoints += subEval.getPointsMax();
        }

        return totalMaxPoints;
    }

    /**
     * Calcule la note d'une sous-évaluation
     */
    private static double calculateSubEvaluationGrade(Evaluation subEvaluation, List<Note> notes) {
        if (notes == null || notes.isEmpty()) {
            return 0.0;
        }

        // Pour une sous-évaluation, on prend la note directe
        for (Note note : notes) {
            if (note.getEvaluationId() == subEvaluation.getId()) {
                return GradeUtils.roundToHalf(note.getNote());
            }
        }

        return 0.0;
    }

    /**
     * Calcule la moyenne pondérée d'un cours complet pour un étudiant
     * @param evaluations Toutes les évaluations du cours
     * @param allNotes Toutes les notes de l'étudiant pour ce cours
     * @param forcedGrades Les notes forcées éventuelles
     * @return La moyenne pondérée du cours
     */
    public static double calculateCourseAverage(List<Evaluation> evaluations, List<Note> allNotes, List<ForcedGrade> forcedGrades) {
        if (evaluations == null || evaluations.isEmpty()) {
            return 0.0;
        }

        double totalWeightedPoints = 0.0;
        double totalWeight = 0.0;

        for (Evaluation evaluation : evaluations) {
            // Ne traiter que les évaluations principales (pas les sous-évaluations)
            if (evaluation.getParentId() == 0) {
                // Vérifier s'il y a une note forcée pour cette évaluation
                ForcedGrade forcedGrade = findForcedGrade(forcedGrades, evaluation.getId());

                if (forcedGrade != null) {
                    // Utiliser la note forcée
                    double weight = evaluation.getPointsMax();
                    totalWeightedPoints += forcedGrade.getForcedGrade() * weight;
                    totalWeight += weight;
                } else {
                    // Calculer la moyenne normale
                    List<Note> evaluationNotes = filterNotesByEvaluation(allNotes, evaluation.getId());
                    double average = calculateStudentAverage(evaluation, evaluationNotes, null, evaluations);
                    double weight = evaluation.getPointsMax();
                    totalWeightedPoints += average * weight;
                    totalWeight += weight;
                }
            }
        }

        if (totalWeight == 0) {
            return 0.0;
        }

        double courseAverage = totalWeightedPoints / totalWeight;
        return GradeUtils.roundToHalf(courseAverage);
    }

    /**
     * Trouve une note forcée pour une évaluation donnée
     */
    private static ForcedGrade findForcedGrade(List<ForcedGrade> forcedGrades, long evaluationId) {
        if (forcedGrades == null) return null;

        for (ForcedGrade forcedGrade : forcedGrades) {
            if (forcedGrade.getEvaluationId() == evaluationId) {
                return forcedGrade;
            }
        }
        return null;
    }

    /**
     * Filtre les notes par évaluation
     */
    private static List<Note> filterNotesByEvaluation(List<Note> allNotes, long evaluationId) {
        return allNotes.stream()
                .filter(note -> note.getEvaluationId() == evaluationId)
                .collect(java.util.stream.Collectors.toList());
    }
}
