package com.example.shinoharanaoki.thetimer5;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shinoharanaoki on 2015/12/23.
 */
public class TaskDataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "task_data";
    private static final int DATABASE_VERSION = 1;

    public TaskDataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //create table
        db.execSQL(TaskDao.CREATE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

}
