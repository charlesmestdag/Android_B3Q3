package com.mestdag.gestionpointsetudiants.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.mestdag.gestionpointsetudiants.model.Student;
import java.util.List;

@Dao
public interface StudentDao {
    @Insert
    void insert(Student student);

    @Query("SELECT * FROM student_table WHERE className = :className")
    List<Student> getStudentsByClass(String className);
    
    @Query("SELECT * FROM student_table WHERE id = :studentId")
    Student getStudentById(long studentId);
    
    @Query("SELECT * FROM student_table")
    List<Student> getAllStudents();
    
    @androidx.room.Delete
    void delete(Student student);
    
    @Query("DELETE FROM student_table WHERE id = :studentId")
    void deleteById(long studentId);
}