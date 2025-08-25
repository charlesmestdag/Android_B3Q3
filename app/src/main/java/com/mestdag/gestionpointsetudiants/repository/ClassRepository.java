package com.mestdag.gestionpointsetudiants.repository;

import android.content.Context;
import com.mestdag.gestionpointsetudiants.DAO.ClassDao;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.ClassEntity;
import java.util.List;

public class ClassRepository {
    private final ClassDao classDao;

    public ClassRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        this.classDao = db.classDao();
    }

    public ClassRepository(ClassDao classDao) { this.classDao = classDao; }

    public List<ClassEntity> getAllClasses() { return classDao.getAllClasses(); }
    public void insert(ClassEntity entity) { classDao.insert(entity); }
    public void delete(ClassEntity entity) { classDao.delete(entity); }
}


