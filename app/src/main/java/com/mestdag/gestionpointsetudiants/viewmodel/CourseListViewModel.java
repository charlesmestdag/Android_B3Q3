package com.mestdag.gestionpointsetudiants.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.Course;
import com.mestdag.gestionpointsetudiants.repository.CourseRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CourseListViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final CourseRepository courseRepository;
    private final MutableLiveData<List<Course>> coursesLiveData = new MutableLiveData<>(new ArrayList<>());
    private String className = "";
    private boolean initialized = false;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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
        executor.execute(() -> {
            try {
                List<Course> courses = courseRepository.getCoursesByClass(className);
                coursesLiveData.postValue(courses);
            } catch (Exception ignored) {}
        });
    }

    public void addCourse(String name) {
        executor.execute(() -> {
            try {
                Course newCourse = new Course();
                newCourse.setName(name != null ? name : "");
                newCourse.setClassName(className);
                courseRepository.insert(newCourse);
                load();
            } catch (Exception ignored) {}
        });
    }

    public void deleteCourse(Course course) {
        executor.execute(() -> {
            try {
                courseRepository.delete(course);
                load();
            } catch (Exception ignored) {}
        });
    }
}


