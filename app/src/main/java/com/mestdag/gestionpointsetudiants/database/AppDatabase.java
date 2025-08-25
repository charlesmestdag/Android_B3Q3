package com.mestdag.gestionpointsetudiants.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.mestdag.gestionpointsetudiants.DAO.ClassDao;
import com.mestdag.gestionpointsetudiants.DAO.StudentDao;
import com.mestdag.gestionpointsetudiants.DAO.CourseDao;
import com.mestdag.gestionpointsetudiants.DAO.EvaluationDao;
import com.mestdag.gestionpointsetudiants.DAO.NoteDao;
import com.mestdag.gestionpointsetudiants.DAO.ForcedGradeDao;

import com.mestdag.gestionpointsetudiants.model.ClassEntity;
import com.mestdag.gestionpointsetudiants.model.Student;
import com.mestdag.gestionpointsetudiants.model.Course;
import com.mestdag.gestionpointsetudiants.model.Evaluation;
import com.mestdag.gestionpointsetudiants.model.Note;
import com.mestdag.gestionpointsetudiants.model.ForcedGrade;


@Database(entities = {ClassEntity.class, Student.class, Course.class, Evaluation.class, Note.class, ForcedGrade.class}, version = 8, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ClassDao classDao();
    public abstract StudentDao studentDao();
    public abstract CourseDao courseDao();
    public abstract EvaluationDao evaluationDao();
    public abstract NoteDao noteDao();
    public abstract ForcedGradeDao forcedGradeDao();


    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(android.content.Context context) {
        if (instance == null) {
            instance = androidx.room.Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "gestion_points_database")
                    .allowMainThreadQueries() // Permet les requêtes sur le thread principal (pour le debug)
                    .fallbackToDestructiveMigration() // Recrée la DB si nécessaire
                    .addCallback(new androidx.room.RoomDatabase.Callback() {
                        @Override
                        public void onCreate(androidx.sqlite.db.SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Désactiver temporairement les foreign keys pour les tests
                            db.execSQL("PRAGMA foreign_keys=OFF");
                        }
                        
                        @Override
                        public void onOpen(androidx.sqlite.db.SupportSQLiteDatabase db) {
                            super.onOpen(db);
                            // Désactiver les foreign keys à chaque ouverture
                            db.execSQL("PRAGMA foreign_keys=OFF");
                        }
                    })
                    .build();
        }
        return instance;
    }
}