package com.mestdag.gestionpointsetudiants.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.mestdag.gestionpointsetudiants.model.Evaluation;
import com.mestdag.gestionpointsetudiants.utils.EvaluationFactory;
import java.util.List;

@Dao
public interface EvaluationDao {
    @Insert
    long insert(Evaluation evaluation);

    @Query("SELECT * FROM evaluation_table WHERE id = :evaluationId")
    Evaluation getEvaluationById(long evaluationId);

    @Query("SELECT * FROM evaluation_table WHERE course_id = :courseId AND parent_id = :parentId")
    List<Evaluation> getEvaluationsByCourseAndParent(long courseId, long parentId);
    
    @Query("SELECT * FROM evaluation_table WHERE course_id = :courseId")
    List<Evaluation> getEvaluationsByCourse(long courseId);

    @Query("SELECT EXISTS(SELECT 1 FROM evaluation_table WHERE parent_id = :evalId)")
    boolean hasSubEvaluations(long evalId);
    
    @Query("SELECT * FROM evaluation_table WHERE course_id = :courseId AND parent_id = 0")
    List<Evaluation> getMainEvaluationsByCourse(long courseId);
    
    @Query("SELECT * FROM evaluation_table WHERE parent_id = :parentId")
    List<Evaluation> getSubEvaluationsByParent(long parentId);
    
    @Transaction
    default List<Evaluation> getMainEvaluationsWithSubs(long courseId) {
        List<Evaluation> allEvaluations = getEvaluationsByCourse(courseId);
        return EvaluationFactory.organizeEvaluationsHierarchically(allEvaluations);
    }
    
    @Query("SELECT COUNT(*) FROM evaluation_table WHERE course_id = :courseId AND parent_id = 0")
    int getMainEvaluationCount(long courseId);
    
    @Query("SELECT COUNT(*) FROM evaluation_table WHERE parent_id = :parentId")
    int getSubEvaluationCount(long parentId);
}