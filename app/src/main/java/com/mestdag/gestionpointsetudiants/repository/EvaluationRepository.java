package com.mestdag.gestionpointsetudiants.repository;

import android.content.Context;
import com.mestdag.gestionpointsetudiants.DAO.EvaluationDao;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.Evaluation;
import java.util.List;

public class EvaluationRepository {
    private final EvaluationDao evaluationDao;

    public EvaluationRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        this.evaluationDao = db.evaluationDao();
    }

    public EvaluationRepository(EvaluationDao evaluationDao) { this.evaluationDao = evaluationDao; }

    public long insert(Evaluation evaluation) { return evaluationDao.insert(evaluation); }
    public List<Evaluation> getEvaluationsByCourse(long courseId) { return evaluationDao.getEvaluationsByCourse(courseId); }
    public boolean hasSubEvaluations(long evaluationId) { return evaluationDao.hasSubEvaluations(evaluationId); }
    public Evaluation getEvaluationById(long id) { return evaluationDao.getEvaluationById(id); }
}


