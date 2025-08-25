package com.mestdag.gestionpointsetudiants.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.mestdag.gestionpointsetudiants.model.Course;
import java.util.List;

@Dao
public interface CourseDao {
    @Insert
    void insert(Course course);

    @Query("SELECT * FROM course_table WHERE id = :courseId")
    Course getCourseById(long courseId);

    @Query("SELECT * FROM course_table WHERE className = :className")
    List<Course> getCoursesByClass(String className);

    @Query("SELECT * FROM course_table")
    List<Course> getAllCourses();

    @Delete
    void delete(Course course);

    @Query("DELETE FROM course_table WHERE id = :courseId")
    void deleteById(long courseId);
}
