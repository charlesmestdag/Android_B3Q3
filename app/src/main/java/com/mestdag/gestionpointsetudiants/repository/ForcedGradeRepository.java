package com.mestdag.gestionpointsetudiants.repository;

import android.content.Context;
import com.mestdag.gestionpointsetudiants.DAO.ForcedGradeDao;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.ForcedGrade;
import java.util.List;

public class ForcedGradeRepository {
    private final ForcedGradeDao forcedGradeDao;

    public ForcedGradeRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        this.forcedGradeDao = db.forcedGradeDao();
    }

    public ForcedGradeRepository(ForcedGradeDao forcedGradeDao) { this.forcedGradeDao = forcedGradeDao; }

    public void insert(ForcedGrade forcedGrade) { forcedGradeDao.insert(forcedGrade); }
    public void deleteForcedGrade(long evaluationId, long studentId) { forcedGradeDao.deleteForcedGrade(evaluationId, studentId); }
    public ForcedGrade getForcedGrade(long evaluationId, long studentId) { return forcedGradeDao.getForcedGrade(evaluationId, studentId); }
    public List<ForcedGrade> getForcedGradesByEvaluation(long evaluationId) { return forcedGradeDao.getForcedGradesByEvaluation(evaluationId); }
}


