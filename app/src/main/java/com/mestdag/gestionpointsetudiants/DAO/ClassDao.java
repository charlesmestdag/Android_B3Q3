package com.mestdag.gestionpointsetudiants.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.mestdag.gestionpointsetudiants.model.ClassEntity;
import java.util.List;

@Dao
public interface ClassDao {
    @Insert
    void insert(ClassEntity classEntity);

    @Query("SELECT * FROM class_table")
    List<ClassEntity> getAllClasses();
    
    @androidx.room.Delete
    void delete(ClassEntity classEntity);
    
    @Query("DELETE FROM class_table WHERE name = :className")
    void deleteByName(String className);
}