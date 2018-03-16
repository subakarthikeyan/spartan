package com.example.ace.spartan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.ExecutionException;

/**
 * Created by ace on 17-Dec-17.
 */

public class Activity_Identifiers extends SQLiteOpenHelper {
    String creates="create table act(keys int(2) )";
    public Activity_Identifiers(Context context) {
        super(context,"mydb", null, 2);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String s="drop table if exists act";
        db.execSQL(s);
        db.execSQL(creates);
        for(int i=0;i<9;i++){
            System.out.println("TABLE CREATED!!!");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
String s="drop table if exists act";
        db.execSQL(s);
        onCreate(db);

    }

    public boolean inserts(int n){
onCreate(getWritableDatabase());
     SQLiteDatabase db=getWritableDatabase();

        try {

            ContentValues localContentValues = new ContentValues();
            localContentValues.put("keys",n);
            db.insert("act", null, localContentValues);
            db.close();
            for(int i=0;i<9;i++){
                System.out.println("VALUE INSERTED!!!");
            }

            return true;
        }catch (Exception e){
System.out.println(e);
        return false;
        }
    }
    public int getVal(){
        SQLiteDatabase db=getReadableDatabase();
        String q="select * from act";
        String d="delete from act";
       Cursor cursor=db.rawQuery( q,null);
       int n=-1;
        if (cursor.getCount() < 1)
        {
            cursor.close();
            return n;
        }
        cursor.moveToFirst();
        n = cursor.getInt(cursor.getColumnIndex("keys"));
        cursor.close();
        return n;
    }

}
