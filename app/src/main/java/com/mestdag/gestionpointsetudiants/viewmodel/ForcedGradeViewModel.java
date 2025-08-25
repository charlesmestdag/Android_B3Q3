package com.mestdag.gestionpointsetudiants.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.ForcedGrade;
import com.mestdag.gestionpointsetudiants.model.ForcedGradeRepository;
import java.util.ArrayList;
import java.util.List;

public class ForcedGradeViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final ForcedGradeRepository forcedGradeRepository;
    private final MutableLiveData<List<ForcedGrade>> forcedGradesLiveData = new MutableLiveData<>(new ArrayList<>());
    private long evaluationId = -1;

    public ForcedGradeViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
        forcedGradeRepository = new ForcedGradeRepository(database.forcedGradeDao());
    }

    public LiveData<List<ForcedGrade>> getForcedGrades() { return forcedGradesLiveData; }

    public void init(long evaluationIdArg) {
        this.evaluationId = evaluationIdArg;
        load();
    }

    public void load() {
        if (evaluationId == -1) return;
        new Thread(() -> {
            try {
                List<ForcedGrade> grades = forcedGradeRepository.getForcedGradesByEvaluation(evaluationId);
                forcedGradesLiveData.postValue(grades);
            } catch (Exception ignored) {}
        }).start();
    }

    public void addOrUpdate(long studentId, double grade, String reason) {
        new Thread(() -> {
            try {
                forcedGradeRepository.insert(new ForcedGrade(evaluationId, studentId, grade, reason));
                load();
            } catch (Exception ignored) {}
        }).start();
    }

    public void remove(long studentId) {
        new Thread(() -> {
            try {
                forcedGradeRepository.deleteForcedGrade(evaluationId, studentId);
                load();
            } catch (Exception ignored) {}
        }).start();
    }
}


