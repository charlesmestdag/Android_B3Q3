package com.mestdag.gestionpointsetudiants.repository;

import android.content.Context;
import com.mestdag.gestionpointsetudiants.DAO.NoteDao;
import com.mestdag.gestionpointsetudiants.database.AppDatabase;
import com.mestdag.gestionpointsetudiants.model.Note;
import java.util.List;

public class NoteRepository {
    private final NoteDao noteDao;

    public NoteRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        this.noteDao = db.noteDao();
    }

    public NoteRepository(NoteDao noteDao) { this.noteDao = noteDao; }

    public void insert(Note note) { noteDao.insert(note); }
    public void insertOrUpdate(Note note) { noteDao.insertOrUpdate(note); }
    public List<Note> getAllNotes() { return noteDao.getAllNotes(); }
    public List<Note> getNotesByStudentAndCourse(long studentId, long courseId) { return noteDao.getNotesByStudentAndCourse(studentId, courseId); }
}


