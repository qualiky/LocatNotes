package com.example.locatnotes.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.locatnotes.models.LocatNotesModel
import java.sql.SQLException

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "LocatNotesDatabase"
        private const val DATABASE_VERSION = 1
        private const val DATABASE_TABLE_NAME = "LocatNotesTable"


        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = ("CREATE TABLE " + DATABASE_TABLE_NAME + " ("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " DOUBLE,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $DATABASE_TABLE_NAME")
        onCreate(db)
    }

    fun addLocatNotes(locatNotesModel: LocatNotesModel) : Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE,locatNotesModel.title)
        contentValues.put(KEY_IMAGE, locatNotesModel.imageUri)
        contentValues.put(KEY_DESCRIPTION, locatNotesModel.description)
        contentValues.put(KEY_DATE, locatNotesModel.date)
        contentValues.put(KEY_LOCATION, locatNotesModel.location)
        contentValues.put(KEY_LATITUDE, locatNotesModel.latitude)
        contentValues.put(KEY_LONGITUDE, locatNotesModel.longitude)

        val result = db.insert(DATABASE_TABLE_NAME, null, contentValues)
        db.close()

        return result
    }
    fun getLocatNotes() : ArrayList<LocatNotesModel> {

        val notesList = ArrayList<LocatNotesModel>()
        val db = this.readableDatabase

        val query = "SELECT * FROM $DATABASE_TABLE_NAME"

        try {

            val cursor = db.rawQuery(query,null)

            if(cursor.moveToFirst()) {
                do {
                    val locat = LocatNotesModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                    )
                    notesList.add(locat)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLException) {

        }
    }
}