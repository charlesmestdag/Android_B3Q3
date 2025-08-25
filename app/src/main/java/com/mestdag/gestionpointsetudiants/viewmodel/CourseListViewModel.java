package com.mestdag.gestionpointsetudiants.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.Course;
import com.mestdag.gestionpointsetudiants.model.CourseRepository;
import java.util.ArrayList;
import java.util.List;

public class CourseListViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final CourseRepository courseRepository;
    private final MutableLiveData<List<Course>> coursesLiveData = new MutableLiveData<>(new ArrayList<>());
    private String className = "";
    private boolean initialized = false;

    public CourseListViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
        courseRepository = new CourseRepository(application.getApplicationContext());
    }

    public LiveData<List<Course>> getCourses() { return coursesLiveData; }

    public void init(String classNameArg) {
        if (initialized) return;
        this.className = classNameArg != null ? classNameArg : "";
        load();
        initialized = true;
    }

    public void load() {
        if (className == null || className.isEmpty()) return;
        new Thread(() -> {
            try {
                List<Course> courses = courseRepository.getCoursesByClass(className);
                coursesLiveData.postValue(courses);
            } catch (Exception ignored) {}
        }).start();
    }

    public void addCourse(String name) {
        new Thread(() -> {
            try {
                Course newCourse = new Course();
                newCourse.setName(name != null ? name : "");
                newCourse.setClassName(className);
                courseRepository.insert(newCourse);
                load();
            } catch (Exception ignored) {}
        }).start();
    }

    public void deleteCourse(Course course) {
        new Thread(() -> {
            try {
                courseRepository.delete(course);
                load();
            } catch (Exception ignored) {}
        }).start();
    }
}


