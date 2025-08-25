package com.mestdag.gestionpointsetudiants.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.Student;
import com.mestdag.gestionpointsetudiants.repository.StudentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentListViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final StudentRepository studentRepository;
    private final MutableLiveData<List<Student>> studentsLiveData = new MutableLiveData<>(new ArrayList<>());
    private String className = "";
    private boolean initialized = false;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public StudentListViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
        studentRepository = new StudentRepository(application.getApplicationContext());
    }

    public LiveData<List<Student>> getStudents() { return studentsLiveData; }

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
                List<Student> students = studentRepository.getStudentsByClass(className);
                studentsLiveData.postValue(students);
            } catch (Exception ignored) {}
        });
    }

    public void addStudent(String lastName, String firstName) {
        executor.execute(() -> {
            try {
                Student newStudent = new Student();
                newStudent.setFirstName(firstName != null ? firstName : "");
                newStudent.setLastName(lastName != null ? lastName : "");
                newStudent.setClassName(className);
                studentRepository.insert(newStudent);
                load();
            } catch (Exception ignored) {}
        });
    }

    public void deleteStudent(Student student) {
        executor.execute(() -> {
            try {
                studentRepository.delete(student);
                load();
            } catch (Exception ignored) {}
        });
    }
}


