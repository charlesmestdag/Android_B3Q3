package com.mestdag.gestionpointsetudiants.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.mestdag.gestionpointsetudiants.model.Evaluation;
import com.mestdag.gestionpointsetudiants.model.Note;
import com.mestdag.gestionpointsetudiants.model.Course;
import com.mestdag.gestionpointsetudiants.model.Student;
import com.mestdag.gestionpointsetudiants.repository.EvaluationRepository;
import com.mestdag.gestionpointsetudiants.repository.NoteRepository;
import com.mestdag.gestionpointsetudiants.repository.StudentRepository;
import com.mestdag.gestionpointsetudiants.repository.CourseRepository;
import com.mestdag.gestionpointsetudiants.repository.ForcedGradeRepository;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.utils.EvaluationFactory;
import com.mestdag.gestionpointsetudiants.utils.EvaluationCalculator;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel pour gérer les évaluations - Améliore l'architecture MVC
 */
public class EvaluationViewModel extends ViewModel {
    
    private MutableLiveData<List<Evaluation>> evaluations;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Double> studentCourseAverage;
    private MutableLiveData<Double> classCourseAverage;
    private AppDatabase database;
    private EvaluationRepository evaluationRepository;
    private NoteRepository noteRepository;
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private ForcedGradeRepository forcedGradeRepository;
    private long courseId;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public EvaluationViewModel() {
        evaluations = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
        studentCourseAverage = new MutableLiveData<>(0.0);
        classCourseAverage = new MutableLiveData<>(0.0);
    }
    
    public void setDatabase(AppDatabase database) {
        this.database = database;
        this.evaluationRepository = new EvaluationRepository(database.evaluationDao());
        this.noteRepository = new NoteRepository(database.noteDao());
        this.studentRepository = new StudentRepository(database.studentDao());
        this.courseRepository = new CourseRepository(database.courseDao());
        this.forcedGradeRepository = new ForcedGradeRepository(database.forcedGradeDao());
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

    public LiveData<Double> getStudentCourseAverage() { return studentCourseAverage; }
    public LiveData<Double> getClassCourseAverage() { return classCourseAverage; }
    
    /**
     * Charge les évaluations du cours
     */
    public void loadEvaluations() {
        if (database == null || courseId == -1) return;
        
        isLoading.postValue(true);
        errorMessage.postValue(null);
        
        executor.execute(() -> {
            try {
                List<Evaluation> rawEvaluations = evaluationRepository.getEvaluationsByCourse(courseId);
                List<Evaluation> typedEvaluations = EvaluationFactory.convertToTypedEvaluations(rawEvaluations);
                // Organiser l'affichage: chaque évaluation principale suivie de ses sous-évaluations
                List<Evaluation> displayList = new java.util.ArrayList<>();
                for (Evaluation eval : typedEvaluations) {
                    if (eval.getParentId() == 0) {
                        displayList.add(eval);
                        // ajouter les sous-évaluations directement après
                        for (Evaluation sub : typedEvaluations) {
                            if (sub.getParentId() == eval.getId()) {
                                displayList.add(sub);
                            }
                        }
                    }
                }
                evaluations.postValue(displayList);
            } catch (Exception e) {
                errorMessage.postValue("Erreur lors du chargement: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }
    
    /**
     * Insère une évaluation et retourne son ID
     */
    public long insertEvaluation(Evaluation evaluation) {
        if (database == null) return -1;
        
        try {
            return evaluationRepository.insert(evaluation);
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
                noteRepository.insert(note);
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
            return courseRepository.getCourseById(courseId);
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
            return studentRepository.getStudentsByClass(className);
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
            return evaluationRepository.getEvaluationsByCourse(courseId);
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
        
        isLoading.postValue(true);
        errorMessage.postValue(null);
        
        executor.execute(() -> {
            try {
                Evaluation newEvaluation = EvaluationFactory.createMainEvaluation(name, courseId);
                evaluationRepository.insert(newEvaluation);
                
                // Recharger les évaluations
                loadEvaluations();
            } catch (Exception e) {
                errorMessage.postValue("Erreur lors de l'ajout: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }
    
    /**
     * Ajoute une sous-évaluation
     */
    public void addSubEvaluation(String name, double points, long parentId) {
        if (database == null || courseId == -1) return;
        
        isLoading.postValue(true);
        errorMessage.postValue(null);
        
        executor.execute(() -> {
            try {
                Evaluation newEvaluation = EvaluationFactory.createSubEvaluation(name, points, parentId, courseId);
                evaluationRepository.insert(newEvaluation);
                
                // Recharger les évaluations
                loadEvaluations();
            } catch (Exception e) {
                errorMessage.postValue("Erreur lors de l'ajout: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }
    
    /**
     * Calcule les statistiques du cours
     */
    public void calculateCourseStatistics() {
        if (database == null || courseId == -1) return;
        
        executor.execute(() -> {
            try {
                List<Evaluation> currentEvaluations = evaluations.getValue();
                List<Note> allNotes = noteRepository.getAllNotes();
                
                if (currentEvaluations != null) {
                    double totalPoints = EvaluationCalculator.calculateCourseTotalPoints(currentEvaluations, courseId);
                    // Ici on pourrait émettre les statistiques via LiveData
                }
            } catch (Exception e) {
                errorMessage.postValue("Erreur lors du calcul: " + e.getMessage());
            }
        });
    }

    public void computeCourseAverages(long studentId) {
        if (database == null || courseId == -1) return;
        executor.execute(() -> {
            try {
                List<Evaluation> allEvaluations = evaluationRepository.getEvaluationsByCourse(courseId);
                List<Evaluation> mainEvaluations = new java.util.ArrayList<>();
                for (Evaluation e : allEvaluations) if (e.getParentId() == 0) mainEvaluations.add(e);

                // Student average: mean of per-main-evaluation weighted averages
                List<Note> studentNotes = noteRepository.getNotesByStudentAndCourse(studentId, courseId);
                double studentSum = 0.0;
                int studentCount = 0;
                for (Evaluation mainEval : mainEvaluations) {
                    com.mestdag.gestionpointsetudiants.model.ForcedGrade fg = forcedGradeRepository.getForcedGrade(mainEval.getId(), studentId);
                    double perEvalAvg = com.mestdag.gestionpointsetudiants.utils.WeightedGradeCalculator.calculateStudentAverage(mainEval, studentNotes, fg, allEvaluations);
                    if (perEvalAvg > 0) { studentSum += perEvalAvg; studentCount++; }
                }
                studentCourseAverage.postValue(studentCount > 0 ? studentSum / studentCount : 0.0);

                // Class average: mean of students' course averages (each is mean of per-evaluation averages)
                Course course = courseRepository.getCourseById(courseId);
                if (course != null) {
                    List<Student> students = studentRepository.getStudentsByClass(course.getClassName());
                    double classSum = 0.0;
                    int classCount = 0;
                    for (Student s : students) {
                        List<Note> sNotes = noteRepository.getNotesByStudentAndCourse(s.getId(), courseId);
                        double sSum = 0.0;
                        int sCount = 0;
                        for (Evaluation mainEval : mainEvaluations) {
                            com.mestdag.gestionpointsetudiants.model.ForcedGrade fg = forcedGradeRepository.getForcedGrade(mainEval.getId(), s.getId());
                            double perEvalAvg = com.mestdag.gestionpointsetudiants.utils.WeightedGradeCalculator.calculateStudentAverage(mainEval, sNotes, fg, allEvaluations);
                            if (perEvalAvg > 0) { sSum += perEvalAvg; sCount++; }
                        }
                        if (sCount > 0) { classSum += (sSum / sCount); classCount++; }
                    }
                    classCourseAverage.postValue(classCount > 0 ? classSum / classCount : 0.0);
                }
            } catch (Exception e) {
                errorMessage.postValue("Erreur moyennes cours: " + e.getMessage());
            }
        });
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
