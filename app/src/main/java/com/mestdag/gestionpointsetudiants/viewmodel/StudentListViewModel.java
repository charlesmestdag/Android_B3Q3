package com.mestdag.gestionpointsetudiants.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.Student;
import com.mestdag.gestionpointsetudiants.model.StudentRepository;
import java.util.ArrayList;
import java.util.List;

public class StudentListViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final StudentRepository studentRepository;
    private final MutableLiveData<List<Student>> studentsLiveData = new MutableLiveData<>(new ArrayList<>());
    private String className = "";
    private boolean initialized = false;

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
        new Thread(() -> {
            try {
                List<Student> students = studentRepository.getStudentsByClass(className);
                studentsLiveData.postValue(students);
            } catch (Exception ignored) {}
        }).start();
    }

    public void addStudent(String lastName, String firstName) {
        new Thread(() -> {
            try {
                Student newStudent = new Student();
                newStudent.setFirstName(firstName != null ? firstName : "");
                newStudent.setLastName(lastName != null ? lastName : "");
                newStudent.setClassName(className);
                studentRepository.insert(newStudent);
                load();
            } catch (Exception ignored) {}
        }).start();
    }

    public void deleteStudent(Student student) {
        new Thread(() -> {
            try {
                studentRepository.delete(student);
                load();
            } catch (Exception ignored) {}
        }).start();
    }
}


