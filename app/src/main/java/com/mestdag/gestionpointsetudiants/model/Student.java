package com.mestdag.gestionpointsetudiants.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(tableName = "student_table", 
        indices = {@androidx.room.Index("className")})
public class Student {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String firstName;
    private String lastName;
    private String className; // Le nom de la classe de l'étudiant (ex: "BA1", "MA2")

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}