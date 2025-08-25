package com.mestdag.gestionpointsetudiants.repository;

import android.content.Context;
import com.mestdag.gestionpointsetudiants.DAO.CourseDao;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.Course;
import java.util.List;

public class CourseRepository {
    private final CourseDao courseDao;

    public CourseRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        this.courseDao = db.courseDao();
    }

    public CourseRepository(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    public List<Course> getCoursesByClass(String className) { return courseDao.getCoursesByClass(className); }
    public Course getCourseById(long courseId) { return courseDao.getCourseById(courseId); }
    public void insert(Course course) { courseDao.insert(course); }
    public void delete(Course course) { courseDao.delete(course); }
}


