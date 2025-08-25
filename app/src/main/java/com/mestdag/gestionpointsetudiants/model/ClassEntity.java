package com.mestdag.gestionpointsetudiants.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "class_table")
public class ClassEntity {
    @PrimaryKey
    @NonNull
    private String name; // Ex. "BA1", "BA2" - utilisé comme clé primaire

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}