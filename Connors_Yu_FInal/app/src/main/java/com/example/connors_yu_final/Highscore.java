package com.example.connors_yu_final;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

public class Highscore extends AppCompatActivity {

    TextView voiceHighscore, motionHighscore;
    private SQLiteDatabase db = null;
    private DatabaseOpenHelper dbHelper = null;
    final static String DBNAME = "highscoreTable";
    final static String USERINITIAL = "userinitial";
    final static String HIGHSCORE = "highscore";
    final static String ID = "_id";
    final static String[] columns = {ID, USERINITIAL, HIGHSCORE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        voiceHighscore = findViewById(R.id.voiceHighscore);
        motionHighscore = findViewById(R.id.motionHighscore);
        dbHelper = new DatabaseOpenHelper(this);
        // dbHelper.deleteDatabase();
        db = dbHelper.getWritableDatabase();
        Cursor c = readDB();
        c.moveToPosition(0);
        voiceHighscore.setText("Voice high score is " + c.getInt(2) + " by " + c.getString(1));
        c.moveToPosition(1);
        motionHighscore.setText("Motion high score is " +c.getInt(2) + " by " + c.getString(1));


    }

    private Cursor readDB() {
        return db.query(dbHelper.DBNAME, columns, null,null, null, null,
                null);
    }
    public void deleteHighScore(View v){
        dbHelper.deleteDatabase();
        voiceHighscore.setText("Voice high score is " + 0 + " by no one");
        motionHighscore.setText("Motion high score is " + 0 + " by no one");
    }

}
