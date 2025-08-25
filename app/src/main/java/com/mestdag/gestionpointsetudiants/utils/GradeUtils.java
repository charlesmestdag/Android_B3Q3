package com.mestdag.gestionpointsetudiants.utils;

public class GradeUtils {
    public static double roundToHalf(double value) {
        return Math.round(value * 2) / 2.0;
    }

    public static double calculateAverage(double[] notes, double[] weights) {
        double sum = 0;
        double totalWeight = 0;
        for (int i = 0; i < notes.length; i++) {
            sum += notes[i] * weights[i];
            totalWeight += weights[i];
        }
        return roundToHalf((sum / totalWeight) * 20); // Normalisé sur 20
    }
    
    public static boolean isValidGrade(double grade, double maxPoints) {
        return grade >= 0 && grade <= maxPoints;
    }
    
    public static String formatGrade(double grade) {
        double rounded = roundToHalf(grade);
        if (rounded == (int) rounded) {
            return String.valueOf((int) rounded);
        } else {
            return String.format("%.1f", rounded);
        }
    }
    
    public static boolean isValidGradeFormat(String input) {
        try {
            double grade = Double.parseDouble(input);
            double rounded = roundToHalf(grade);
            return Math.abs(grade - rounded) < 0.01;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}