package com.example.notapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.notapp.model.Note
import com.example.notapp.util.Constants

class NoteDatabase (context: Context):SQLiteOpenHelper(context,Constants.DB_NAME,null,Constants.DB_VERSION),DatabaseService{

    override fun onCreate(db: SQLiteDatabase?) {
        val query  =
            "CREATE TABLE ${Constants.TABLE_NAME}(${Constants.ID} INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, ${Constants.TITLE} TEXT NOT NULL ,${Constants.DESCRIPTION} TEXT NOT NULL,${Constants.TIME} TEXT NOT NULL)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${Constants.TABLE_NAME}")
        onCreate(db)
    }

    override fun saveNote(note: Note) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Constants.TITLE,note.title)
        contentValues.put(Constants.DESCRIPTION,note.description)
        contentValues.put(Constants.TIME,note.time)
        database.insert(Constants.TABLE_NAME,null,contentValues)
        database.close()
    }

    override fun updateNote(note: Note) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Constants.ID,note.id)
        contentValues.put(Constants.TITLE,note.title)
        contentValues.put(Constants.DESCRIPTION,note.description)
        contentValues.put(Constants.TIME,note.time)
        database.update(
            Constants.TABLE_NAME,
            contentValues,
            "${Constants.ID} = ?",
            arrayOf(note.id.toString())
        )
        database.close()
    }

    override fun deleteNote(id:Int) {
        val database = this.writableDatabase
        database.delete(Constants.TABLE_NAME,"${Constants.ID} = ?", arrayOf(id.toString()))
        database.close()
    }

    override fun getAllNotes(): MutableList<Note> {
        val noteList = ArrayList<Note>()
        val database = this.readableDatabase
        val query ="SELECT * FROM ${Constants.TABLE_NAME}"
        val cursor = database.rawQuery(query,null)
        if (cursor.moveToFirst()){
            do{
                val note = Note(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
                )
                noteList.add(note)
            }while (cursor.moveToNext())
        }
        cursor.close()
        database.close()
        return noteList
    }

    override fun deleteAllNotes() {

    }
}