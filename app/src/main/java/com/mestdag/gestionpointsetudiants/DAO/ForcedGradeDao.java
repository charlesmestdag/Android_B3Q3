package com.mestdag.gestionpointsetudiants.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.mestdag.gestionpointsetudiants.model.ForcedGrade;
import java.util.List;

/**
 * DAO pour gérer les notes forcées dans la base de données
 */
@Dao
public interface ForcedGradeDao {
    @Insert
    void insert(ForcedGrade forcedGrade);

    @Update
    void update(ForcedGrade forcedGrade);

    @Delete
    void delete(ForcedGrade forcedGrade);

    @Query("SELECT * FROM forced_grade_table WHERE evaluationId = :evaluationId AND studentId = :studentId")
    ForcedGrade getForcedGrade(long evaluationId, long studentId);

    @Query("SELECT * FROM forced_grade_table WHERE evaluationId = :evaluationId")
    List<ForcedGrade> getForcedGradesByEvaluation(long evaluationId);

    @Query("SELECT * FROM forced_grade_table WHERE studentId = :studentId")
    List<ForcedGrade> getForcedGradesByStudent(long studentId);

    @Query("DELETE FROM forced_grade_table WHERE evaluationId = :evaluationId AND studentId = :studentId")
    void deleteForcedGrade(long evaluationId, long studentId);

    @Query("SELECT COUNT(*) FROM forced_grade_table WHERE evaluationId = :evaluationId")
    int countForcedGradesByEvaluation(long evaluationId);
}
