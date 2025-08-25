package com.mestdag.gestionpointsetudiants.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(tableName = "course_table",
        indices = {@androidx.room.Index("className")})
public class Course {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String className; // Le nom de la classe auquel appartient ce cours (ex: "BA1", "MA2")

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
