package com.mestdag.gestionpointsetudiants.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.ClassEntity;
import com.mestdag.gestionpointsetudiants.repository.ClassRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassListViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final ClassRepository classRepository;
    private final MutableLiveData<List<ClassEntity>> classesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ClassListViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
        classRepository = new ClassRepository(application.getApplicationContext());
    }

    public LiveData<List<ClassEntity>> getClasses() { return classesLiveData; }

    public void load() {
        executor.execute(() -> {
            try {
                List<ClassEntity> classes = classRepository.getAllClasses();
                classesLiveData.postValue(classes);
            } catch (Exception ignored) {}
        });
    }

    public void addClass(String className) {
        executor.execute(() -> {
            try {
                ClassEntity newClass = new ClassEntity();
                newClass.setName(className != null ? className : "");
                classRepository.insert(newClass);
                load();
            } catch (Exception ignored) {}
        });
    }

    public void deleteClass(ClassEntity classEntity) {
        executor.execute(() -> {
            try {
                classRepository.delete(classEntity);
                load();
            } catch (Exception ignored) {}
        });
    }
}


