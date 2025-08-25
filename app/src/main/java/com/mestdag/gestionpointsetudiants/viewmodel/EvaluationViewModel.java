package com.mestdag.gestionpointsetudiants.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.mestdag.gestionpointsetudiants.model.Evaluation;
import com.mestdag.gestionpointsetudiants.model.Note;
import com.mestdag.gestionpointsetudiants.model.Course;
import com.mestdag.gestionpointsetudiants.model.Student;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.utils.EvaluationFactory;
import com.mestdag.gestionpointsetudiants.utils.EvaluationCalculator;
import java.util.List;
import java.util.ArrayList;

/**
 * ViewModel pour gérer les évaluations - Améliore l'architecture MVC
 */
public class EvaluationViewModel extends ViewModel {
    
    private MutableLiveData<List<Evaluation>> evaluations;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;
    private AppDatabase database;
    private long courseId;
    
    public EvaluationViewModel() {
        evaluations = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
    }
    
    public void setDatabase(AppDatabase database) {
        this.database = database;
    }
    
    public void setCourseId(long courseId) {
        this.courseId = courseId;
        loadEvaluations();
    }
    
    public LiveData<List<Evaluation>> getEvaluations() {
        return evaluations;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Charge les évaluations du cours
     */
    public void loadEvaluations() {
        if (database == null || courseId == -1) return;
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        new Thread(() -> {
            try {
                List<Evaluation> rawEvaluations = database.evaluationDao().getEvaluationsByCourse(courseId);
                List<Evaluation> typedEvaluations = EvaluationFactory.convertToTypedEvaluations(rawEvaluations);
                
                evaluations.postValue(typedEvaluations);
            } catch (Exception e) {
                errorMessage.postValue("Erreur lors du chargement: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        }).start();
    }
    
    /**
     * Insère une évaluation et retourne son ID
     */
    public long insertEvaluation(Evaluation evaluation) {
        if (database == null) return -1;
        
        try {
            return database.evaluationDao().insert(evaluation);
        } catch (Exception e) {
            errorMessage.postValue("Erreur lors de l'insertion: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Insère une note
     */
    public void insertNote(Note note) {
        if (database == null) return;
        
        new Thread(() -> {
            try {
                database.noteDao().insert(note);
            } catch (Exception e) {
                errorMessage.postValue("Erreur lors de l'insertion de la note: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Récupère un cours par son ID
     */
    public Course getCourseById(long courseId) {
        if (database == null) return null;
        
        try {
            return database.courseDao().getCourseById(courseId);
        } catch (Exception e) {
            errorMessage.postValue("Erreur lors de la récupération du cours: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Récupère les étudiants d'une classe
     */
    public List<Student> getStudentsByClass(String className) {
        if (database == null) return new ArrayList<>();
        
        try {
            return database.studentDao().getStudentsByClass(className);
        } catch (Exception e) {
            errorMessage.postValue("Erreur lors de la récupération des étudiants: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Récupère les évaluations d'un cours
     */
    public List<Evaluation> getEvaluationsByCourse(long courseId) {
        if (database == null) return new ArrayList<>();
        
        try {
            return database.evaluationDao().getEvaluationsByCourse(courseId);
        } catch (Exception e) {
            errorMessage.postValue("Erreur lors de la récupération des évaluations: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Ajoute une évaluation principale
     */
    public void addMainEvaluation(String name) {
        if (database == null || courseId == -1) return;
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        new Thread(() -> {
            try {
                Evaluation newEvaluation = EvaluationFactory.createMainEvaluation(name, courseId);
                database.evaluationDao().insert(newEvaluation);
                
                // Recharger les évaluations
                loadEvaluations();
            } catch (Exception e) {
                errorMessage.postValue("Erreur lors de l'ajout: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }
    
    /**
     * Ajoute une sous-évaluation
     */
    public void addSubEvaluation(String name, double points, long parentId) {
        if (database == null || courseId == -1) return;
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        new Thread(() -> {
            try {
                Evaluation newEvaluation = EvaluationFactory.createSubEvaluation(name, points, parentId, courseId);
                database.evaluationDao().insert(newEvaluation);
                
                // Recharger les évaluations
                loadEvaluations();
            } catch (Exception e) {
                errorMessage.postValue("Erreur lors de l'ajout: " + e.getMessage());
                isLoading.postValue(false);
            }
        }).start();
    }
    
    /**
     * Calcule les statistiques du cours
     */
    public void calculateCourseStatistics() {
        if (database == null || courseId == -1) return;
        
        new Thread(() -> {
            try {
                List<Evaluation> currentEvaluations = evaluations.getValue();
                List<Note> allNotes = database.noteDao().getAllNotes();
                
                if (currentEvaluations != null) {
                    double totalPoints = EvaluationCalculator.calculateCourseTotalPoints(currentEvaluations, courseId);
                    // Ici on pourrait émettre les statistiques via LiveData
                }
            } catch (Exception e) {
                errorMessage.postValue("Erreur lors du calcul: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Valide une évaluation
     */
    public boolean validateEvaluation(Evaluation evaluation) {
        List<Evaluation> currentEvaluations = evaluations.getValue();
        if (currentEvaluations == null) return false;
        
        return EvaluationFactory.validateEvaluation(evaluation, currentEvaluations);
    }
    
    /**
     * Vérifie si une évaluation peut avoir des sous-évaluations
     */
    public boolean canHaveSubEvaluations(Evaluation evaluation) {
        return evaluation != null && evaluation.canHaveSubEvaluations();
    }
    
    /**
     * Obtient les sous-évaluations d'une évaluation
     */
    public List<Evaluation> getSubEvaluations(Evaluation evaluation) {
        List<Evaluation> currentEvaluations = evaluations.getValue();
        if (currentEvaluations == null) return new ArrayList<>();
        
        return evaluation.getSubEvaluations(currentEvaluations);
    }
}
