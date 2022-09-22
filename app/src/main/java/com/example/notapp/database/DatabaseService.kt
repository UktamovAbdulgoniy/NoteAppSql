package com.example.notapp.database

import com.example.notapp.model.Note

interface DatabaseService {
    // CRUD -> C = Create,R - Read,U = Update , D = Delete
    fun saveNote(note: Note) // create
    fun updateNote(note: Note) // update
    fun deleteNote(id:Int) //delete
    fun getAllNotes():MutableList<Note> // read
    fun deleteAllNotes()
}