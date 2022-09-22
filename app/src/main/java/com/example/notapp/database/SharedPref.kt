package com.example.notapp.database

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context){
    private val mySharedPref:SharedPreferences = context.getSharedPreferences("MyRef",Context.MODE_PRIVATE)
    fun saveManager(save:Boolean){
        val editor = mySharedPref.edit()
        editor.putBoolean("save",save)
        editor.apply()
    }
    fun getManager():Boolean{
        return mySharedPref.getBoolean("save",false)
    }
}