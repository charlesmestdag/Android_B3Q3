package com.mestdag.gestionpointsetudiants.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.Course;
import com.mestdag.gestionpointsetudiants.model.Evaluation;
import com.mestdag.gestionpointsetudiants.model.ForcedGrade;
import com.mestdag.gestionpointsetudiants.model.Note;
import com.mestdag.gestionpointsetudiants.model.Student;
import com.mestdag.gestionpointsetudiants.repository.CourseRepository;
import com.mestdag.gestionpointsetudiants.repository.EvaluationRepository;
import com.mestdag.gestionpointsetudiants.repository.ForcedGradeRepository;
import com.mestdag.gestionpointsetudiants.repository.NoteRepository;
import com.mestdag.gestionpointsetudiants.repository.StudentRepository;
import com.mestdag.gestionpointsetudiants.utils.WeightedGradeCalculator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteEntryViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final CourseRepository courseRepository;
    private final EvaluationRepository evaluationRepository;
    private final ForcedGradeRepository forcedGradeRepository;
    private final NoteRepository noteRepository;
    private final StudentRepository studentRepository;

    private final MutableLiveData<List<StudentGradeInfo>> studentsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<StatsUi> statsLiveData = new MutableLiveData<>(new StatsUi(0, 0, 0));
    private long evaluationId = -1;
    private double evaluationMaxPoints = 20.0;
    private boolean initialized = false;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public NoteEntryViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
        courseRepository = new CourseRepository(database.courseDao());
        evaluationRepository = new EvaluationRepository(database.evaluationDao());
        forcedGradeRepository = new ForcedGradeRepository(database.forcedGradeDao());
        noteRepository = new NoteRepository(database.noteDao());
        studentRepository = new StudentRepository(database.studentDao());
    }

    public LiveData<List<StudentGradeInfo>> getStudents() {
        return studentsLiveData;
    }

    public LiveData<StatsUi> getStats() {
        return statsLiveData;
    }

    public double getEvaluationMaxPoints() {
        return evaluationMaxPoints;
    }

    public void init(long evaluationIdArg) {
        if (initialized) return;
        this.evaluationId = evaluationIdArg;
        load();
        initialized = true;
    }

    public void load() {
        executor.execute(() -> {
            try {
                Evaluation evaluation = evaluationRepository.getEvaluationById(evaluationId);
                if (evaluation == null) return;

                Course course = courseRepository.getCourseById(evaluation.getCourseId());
                if (course == null) return;

                List<Student> allStudents = studentRepository.getStudentsByClass(course.getClassName());
                List<Evaluation> allEvaluations = evaluationRepository.getEvaluationsByCourse(evaluation.getCourseId());

                boolean isMainWithSubs = false;
                if (evaluation.getParentId() == 0) {
                    isMainWithSubs = evaluationRepository.hasSubEvaluations(evaluation.getId());
                }
                if (isMainWithSubs) {
                    evaluationMaxPoints = 20.0;
                }

                List<StudentGradeInfo> gradeInfos = new ArrayList<>();
                double totalGrades = 0;
                int validGradesCount = 0;

                for (Student student : allStudents) {
                    StudentGradeInfo info = new StudentGradeInfo();
                    info.student = student;

                    ForcedGrade forcedGrade = forcedGradeRepository.getForcedGrade(evaluationId, student.getId());
                    if (forcedGrade != null) {
                        info.currentGrade = forcedGrade.getForcedGrade();
                        info.isForced = true;
                        info.forcedGradeReason = forcedGrade.getReason();
                        totalGrades += forcedGrade.getForcedGrade();
                        validGradesCount++;
                    } else {
                        List<Note> studentNotes = noteRepository.getNotesByStudentAndCourse(student.getId(), evaluation.getCourseId());
                        double calculatedGrade = WeightedGradeCalculator.calculateStudentAverage(evaluation, studentNotes, null, allEvaluations);
                        if (calculatedGrade > 0) {
                            info.currentGrade = calculatedGrade;
                            validGradesCount++;
                            totalGrades += calculatedGrade;
                        } else {
                            info.currentGrade = -1;
                        }
                    }

                    gradeInfos.add(info);
                }

                studentsLiveData.postValue(gradeInfos);
                double average = validGradesCount > 0 ? totalGrades / validGradesCount : 0;
                statsLiveData.postValue(new StatsUi(average, validGradesCount, allStudents.size()));
            } catch (Exception ignored) {}
        });
    }

    public void saveGrade(long studentId, double grade) {
        executor.execute(() -> {
            try {
                Note note = new Note();
                note.setEvaluationId(evaluationId);
                note.setStudentId(studentId);
                note.setNote(grade);
                noteRepository.insertOrUpdate(note);
                load();
            } catch (Exception ignored) {}
        });
    }

    public void forceGrade(long studentId, double grade, String reason) {
        executor.execute(() -> {
            try {
                ForcedGrade forcedGrade = new ForcedGrade(evaluationId, studentId, grade, reason);
                forcedGradeRepository.insert(forcedGrade);
                load();
            } catch (Exception ignored) {}
        });
    }

    public void removeForcedGrade(long studentId) {
        executor.execute(() -> {
            try {
                forcedGradeRepository.deleteForcedGrade(evaluationId, studentId);
                load();
            } catch (Exception ignored) {}
        });
    }

    public void reloadStatistics() {
        executor.execute(() -> {
            try {
                Evaluation evaluation = evaluationRepository.getEvaluationById(evaluationId);
                if (evaluation == null) return;
                List<Evaluation> allEvaluations = evaluationRepository.getEvaluationsByCourse(evaluation.getCourseId());
                List<ForcedGrade> forcedGrades = forcedGradeRepository.getForcedGradesByEvaluation(evaluationId);

                List<StudentGradeInfo> current = studentsLiveData.getValue();
                if (current == null) current = new ArrayList<>();

                double totalGrades = 0;
                int validGradesCount = 0;

                for (StudentGradeInfo info : current) {
                    ForcedGrade fgMatch = null;
                    for (ForcedGrade fg : forcedGrades) {
                        if (fg.getStudentId() == info.student.getId()) {
                            fgMatch = fg;
                            break;
                        }
                    }
                    if (fgMatch != null) {
                        totalGrades += fgMatch.getForcedGrade();
                        validGradesCount++;
                    } else {
                        List<Note> studentNotes = noteRepository.getNotesByStudentAndCourse(info.student.getId(), evaluation.getCourseId());
                        double calculatedGrade = WeightedGradeCalculator.calculateStudentAverage(evaluation, studentNotes, null, allEvaluations);
                        if (calculatedGrade > 0) {
                            totalGrades += calculatedGrade;
                            validGradesCount++;
                        }
                    }
                }

                double average = validGradesCount > 0 ? totalGrades / validGradesCount : 0;
                statsLiveData.postValue(new StatsUi(average, validGradesCount, current.size()));
            } catch (Exception ignored) {}
        });
    }

    public static class StudentGradeInfo {
        public Student student;
        public double currentGrade = -1;
        public boolean isForced = false;
        public String forcedGradeReason = "";
    }

    public static class StatsUi {
        public final double average;
        public final int gradedStudents;
        public final int totalStudents;

        public StatsUi(double average, int gradedStudents, int totalStudents) {
            this.average = average;
            this.gradedStudents = gradedStudents;
            this.totalStudents = totalStudents;
        }
    }
}


