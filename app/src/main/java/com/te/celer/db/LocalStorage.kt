package com.te.celer.db

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalStorage() {

    fun saveData(context: Context, key: String, map: Map<String, String>) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(map)
        editor.putString(key, json)
        editor.apply()
    }

    fun getData(context: Context, key: String): Map<String, String>? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(json, type)
    }


    fun removeData(context: Context, key: String) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun removeAllData(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

}