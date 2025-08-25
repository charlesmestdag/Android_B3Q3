package com.mestdag.gestionpointsetudiants.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.mestdag.gestionpointsetudiants.model.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insert(Note note);

    @Query("SELECT * FROM note_table WHERE evaluationId = :evalId AND studentId = :studentId")
    Note getNote(long evalId, long studentId);
    
    @Query("SELECT * FROM note_table WHERE evaluationId = :evalId")
    List<Note> getNotesByEvaluation(long evalId);
    
    @Query("SELECT n.* FROM note_table n " +
           "INNER JOIN evaluation_table e ON n.evaluationId = e.id " +
           "WHERE n.studentId = :studentId AND e.course_id = :courseId")
    List<Note> getNotesByStudentAndCourse(long studentId, long courseId);
    
    @androidx.room.Update
    void update(Note note);
    
    @androidx.room.Delete
    void delete(Note note);
    
    @Query("DELETE FROM note_table WHERE evaluationId = :evalId AND studentId = :studentId")
    void deleteByEvaluationAndStudent(long evalId, long studentId);
    
    @Query("INSERT OR REPLACE INTO note_table (evaluationId, studentId, note) VALUES (:evalId, :studentId, :noteValue)")
    void insertOrUpdate(long evalId, long studentId, double noteValue);
    
    // Méthode helper pour l'insertOrUpdate avec objet Note
    default void insertOrUpdate(Note note) {
        insertOrUpdate(note.getEvaluationId(), note.getStudentId(), note.getNote());
    }
    
    @Query("SELECT * FROM note_table")
    List<Note> getAllNotes();
    
    @Query("SELECT * FROM note_table WHERE studentId = :studentId")
    List<Note> getNotesByStudent(long studentId);
    
    @Query("SELECT COUNT(*) FROM note_table WHERE evaluationId = :evalId")
    int getNoteCountForEvaluation(long evalId);
}