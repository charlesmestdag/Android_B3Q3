package com.mestdag.gestionpointsetudiants.repository;

import android.content.Context;
import com.mestdag.gestionpointsetudiants.DAO.StudentDao;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.Student;
import java.util.List;

public class StudentRepository {
    private final StudentDao studentDao;

    public StudentRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        this.studentDao = db.studentDao();
    }

    public StudentRepository(StudentDao studentDao) { this.studentDao = studentDao; }

    public List<Student> getStudentsByClass(String className) { return studentDao.getStudentsByClass(className); }
    public void insert(Student student) { studentDao.insert(student); }
    public void delete(Student student) { studentDao.delete(student); }
}


