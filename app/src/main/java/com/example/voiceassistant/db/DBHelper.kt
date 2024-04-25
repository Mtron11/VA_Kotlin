package com.example.voiceassistent.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    val TABLE_NAME = "messages"
    val FIELD_ID = "id"
    val FIELD_MESSAGE = "message"
    val FIELD_SEND = "send"
    val FIELD_DATE = "date"

    constructor(context: Context?) : this(context, "MessageDB", null, 1)
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table $TABLE_NAME (" +
                "$FIELD_ID integer primary key, " +
                "$FIELD_MESSAGE text, " +
                "$FIELD_SEND integer, " +
                "$FIELD_DATE text)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists $TABLE_NAME")
        onCreate(db)
    }

}