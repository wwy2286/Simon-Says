package com.example.connors_yu_final;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper{

    final static String DBNAME = "highscoreTable";
    final static String ID = "_id";
    final static String USERINITIAL = "userinitial";
    final static String HIGHSCORE = "highscore";

    final private static String CREATE_CMD =
            "CREATE TABLE "+DBNAME+" (" + ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USERINITIAL + " TEXT NOT NULL, " + HIGHSCORE + " INTEGER NOT NULL)" ;

    final private static Integer VERSION = 1;
    final private Context context;

    public DatabaseOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
        ContentValues values = new ContentValues(2);

        values.put(USERINITIAL, "no one");
        values.put(HIGHSCORE, 0);
        db.insert(DBNAME,null,values);
        values.clear();
        values.put(USERINITIAL, "no one");
        values.put(HIGHSCORE, 0);
        db.insert(DBNAME,null,values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    void deleteDatabase ( ) {
        context.deleteDatabase(DBNAME);
    }

    public void updateScore(int newHighscore, int id){
        SQLiteDatabase db = this.getReadableDatabase();
        //String query = "UPDATE " + DBNAME + " SET " + HIGHSCORE + " = '" + newHighscore + "' WHERE " + ID + " = '" + 1 + "'" + " AND " + HIGHSCORE + " = '" + 0 + "'";
        String query = "UPDATE " + DBNAME + " SET " + HIGHSCORE + " = '" + newHighscore + "' WHERE " + ID + " = '" + id + "'";
        db.execSQL(query);
    }

    public void updateScore(int newHighscore, String name, int id){
        SQLiteDatabase db = this.getReadableDatabase();
        //String query = "UPDATE " + DBNAME + " SET " + HIGHSCORE + " = '" + newHighscore + "' WHERE " + ID + " = '" + 1 + "'" + " AND " + HIGHSCORE + " = '" + 0 + "'";
        String query = "UPDATE " + DBNAME + " SET " + HIGHSCORE + " = '" + newHighscore + "' WHERE " + ID + " = '" + id + "'";
        db.execSQL(query);
         query = "UPDATE " + DBNAME + " SET " + USERINITIAL + " = '" + name + "' WHERE " + ID + " = '" + id + "'";
        db.execSQL(query);

    }
}