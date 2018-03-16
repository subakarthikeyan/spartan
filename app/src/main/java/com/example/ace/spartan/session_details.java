package com.example.ace.spartan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ace on 15-Jan-18.
 */

public class session_details extends SQLiteOpenHelper {
    String creates="create table session(phone varchar(50) primary key,pass text,name text,CheckUser int)";
    public session_details(Context context) {
        super(context,"mydb", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s="drop table if exists session";
        db.execSQL(s);
        db.execSQL(creates);
        for(int i=0;i<9;i++){
            System.out.println("TABLE CREATED!!!");
        }
db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String s="drop table if exists session";
        for(int i=0;i<9;i++){
            System.out.println("TABLE DROPED!!!");
        }
        db.execSQL(s);

        onCreate(db);
db.close();
    }
    public boolean inserts(String ph, String pass, String patientName,int CheckVal){
        onCreate(getWritableDatabase());
        SQLiteDatabase db=getWritableDatabase();

        try {

            ContentValues localContentValues = new ContentValues();
            localContentValues.put("phone",ph);
            localContentValues.put("pass",pass);
            localContentValues.put("name",patientName);
            localContentValues.put("CheckUser",CheckVal);
            db.insert("session", null, localContentValues);
            db.close();
            for(int i=0;i<9;i++){
                System.out.println("VALUE INSERTED!!!");
            }

            return true;
        }catch (Exception e){
           db.close();
            System.out.println(e);
            return false;
        }
    }
public int getCheck(){
    SQLiteDatabase db=getReadableDatabase();
    String q="select CheckUser from session";
    Cursor cursor=db.rawQuery( q,null);
int a=0;
    if(cursor.getCount()<1){
    a=0;
    cursor.close();
    db.close();
    return a;
}
    cursor.moveToFirst();
    a = cursor.getInt(cursor.getColumnIndex("CheckUser"));
cursor.close();
db.close();
return a;
}
    public String[] getVal(){
        SQLiteDatabase db=getReadableDatabase();
        String q="select * from session";

        Cursor cursor=db.rawQuery( q,null);
        String[] n=new String[3];
        if (cursor.getCount() < 1)
        {
            n[0]=null;
            n[1]=null;
            n[2]=null;
            cursor.close();
db.close();
            return n;
        }
        cursor.moveToFirst();
        n[0] = cursor.getString(cursor.getColumnIndex("phone"));
        n[1]=cursor.getString(cursor.getColumnIndex("pass"));
        n[2]=cursor.getString(cursor.getColumnIndex("name"));
        cursor.close();
        db.close();
        return n;
    }

}
