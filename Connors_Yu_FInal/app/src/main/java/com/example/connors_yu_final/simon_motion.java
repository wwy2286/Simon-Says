package com.example.connors_yu_final;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Random;

public class simon_motion extends AppCompatActivity {

    //enumerate the colors into numbers
    private final static int GREEN = 1;
    private final static int RED = 2;
    private final static int YELLOW = 3;
    private final static int BLUE = 4;
    private String userInitial;
    private EditText input = null;

    //the flow of the game should be
    //generate the sequence, play the sequence for the player, player plays it back
    State curr_state = new State("");

    private static Bitmap GreenUnlit, GreenLit;
    private static Bitmap RedUnlit, RedLit;
    private static Bitmap BlueUnlit, BlueLit;
    private static Bitmap YellowUnlit, YellowLit;
    private ImageView gameover;
    Button inputButton, restartButton, instructionButton;

    private static TextView Score, HighScore;

    //the two arrays that will dictate the game
    private static ArrayList<Integer> gameSequence;
    private static ArrayList<Integer> playerSequence;
    private int MAX_NUM=3;

    private static long mLastMove;
    private static long mMoveDelay;
    private static int playSeqCounter;
    private static int colorTouched;
    private static boolean winOrLose;
    private static int scoreCounter;
    private ImageView greenLit, redLit, blueLit, yellowLit;
    private int currHighscore;
    private SQLiteDatabase db = null;
    private DatabaseOpenHelper dbHelper = null;
    final static String DBNAME = "highscoreTable";
    final static String USERINITIAL = "userinitial";
    final static String HIGHSCORE = "highscore";
    final static String ID = "_id";
    final static String[] columns = {ID, USERINITIAL, HIGHSCORE};
     private MediaPlayer redSound, blueSound, greenSound, yellowSound;



    private HashMap<String,Integer> colorToInt = new HashMap<>();
    private ArrayList<Integer> bb;

    private Accelerometer accelerometer;
    private Gyroscope gyroscope;
    Context context;
    buttonHandler SimonButtonHandler = new buttonHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simon);
        context = this;
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        accelerometer=new Accelerometer(this);
        gyroscope = new Gyroscope(this);
        colorToInt.put("RED",2);
        colorToInt.put("GREEN",1);
        colorToInt.put("YELLOW",3);
        colorToInt.put("BLUE",4);
        inputButton = findViewById(R.id.motionButton);
        gameover = findViewById(R.id.gameover);
        Score = (TextView)findViewById(R.id.scoreString);
        restartButton = findViewById(R.id.restartButton);
        restartButton = findViewById(R.id.restartButton);
        greenLit = (ImageView) findViewById(R.id.greenButton);
        redLit = (ImageView) findViewById(R.id.redButton);
        yellowLit = (ImageView) findViewById(R.id.yellowButton);
        blueLit = (ImageView) findViewById(R.id.blueButton);
        HighScore = findViewById(R.id.scoreView);
        instructionButton = findViewById(R.id.instructionButton);
        gameSequence = new ArrayList<Integer>();
        bb = new ArrayList<Integer>();

        dbHelper = new DatabaseOpenHelper(this);
        // dbHelper.deleteDatabase();
        db = dbHelper.getWritableDatabase();
        Cursor c = readDB();
        c.moveToPosition(1);
        int id = c.getInt(0);
        currHighscore = c.getInt(2);
        //gyroscope.unregister();
        //setGyroscopeListener();
        setBitMaps();
        inputButton.setEnabled(false);
        //setAccelerometerListener();

       blueSound = MediaPlayer.create(this, R.raw.blue);
        redSound = MediaPlayer.create(this, R.raw.red);
        greenSound = MediaPlayer.create(this, R.raw.green);
        yellowSound = MediaPlayer.create(this, R.raw.yellow);





//


        //gyroscope.unregister();

        //initGame();
        //gyroscope.unregister();

    }

 private void askInitial(){
     AlertDialog.Builder builder = new AlertDialog.Builder(this);
     builder.setTitle("Enter your initial");

// Set up the input
      input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
     input.setInputType(InputType.TYPE_CLASS_TEXT);

     input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_NUM), new InputFilter.AllCaps() });
     builder.setView(input);

// Set up the buttons
     builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {

            // Toast.makeText(this, "You have achieved a new high score!", Toast.LENGTH_LONG).show();
             userInitial = input.getText().toString();
             if (userInitial.equals("")){
                 Toast.makeText(context, "Initial can not be empty", Toast.LENGTH_LONG).show();
                 askInitial();
             } else if (userInitial.length() != 3) {
                 Toast.makeText(context, "Initial must be three letters", Toast.LENGTH_LONG).show();
                 askInitial();
             }else if (!isStringOnlyAlphabet(userInitial)){

                 Toast.makeText(context, "Initial must contain only letters", Toast.LENGTH_LONG).show();
                 askInitial();

             } else {
                 dbHelper.updateScore(scoreCounter, userInitial,2);
                 Toast.makeText(context, "High Score Added", Toast.LENGTH_LONG).show();
             }

         }
     });
     builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
             dialog.cancel();
         }
     });

     builder.show();
 }
    private Cursor readDB() {
        return db.query(dbHelper.DBNAME, columns, null,null, null, null,
                null);
    }
    private class State {
        String state = "";
        public State() {
        }

        public State(String state) {
            this.state = state;
        }

    }

    public void removeInstruction(View v){
        instructionButton.setVisibility(View.INVISIBLE);
    }
    public void setAccelerometerListener(){
        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {

                Toast.makeText(context, "x = " + tx + " y = " + ty + " z = " + tz , Toast.LENGTH_LONG);
                if(tx>1.0f && ty>1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                  redSound.start();
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    bb.add(2);
//                    if (compareSequence() == false){
//                        curr_state.state = "Done";
//                   }

                    accelerometer.unregister();

                }
                else if(tx>1.0f && ty<-1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                    blueSound.start();
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);

                    bb.add(4);
//                    if (compareSequence() == false){
//                        curr_state.state = "Done";
//                    }
                    accelerometer.unregister();

                }
                else if(tx<-1.0f && ty>1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    greenSound.start();
                    bb.add(1);
//                    if (compareSequence() == false){
//                        curr_state.state = "Done";
//                    }
                    accelerometer.unregister();

                } else if(tx<-1.0f && ty<-1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    yellowSound.start();
                    bb.add(3);
//                    if (compareSequence() == false){
//                        curr_state.state = "Done";
//                    }
                    accelerometer.unregister();

                }

            }
        });

    }

    public void setGyroscopeListener(){
        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {
//                System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
//                Toast.makeText(context, "x = " + tx + " y = " + ty + " z = " + tz , Toast.LENGTH_LONG);
                if(tx>2.0f && ty>2.0f && (curr_state.state.equals( "Guess"))){
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                    bb.add(2);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);

                    gyroscope.unregister();
                }
                else if(tx>2.0f && ty<-2.0f && (curr_state.state.equals(  "Guess"))){
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                    bb.add(4);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    gyroscope.unregister();
                }
                else if(tx<-2.0f && ty>2.0f && (curr_state.state.equals( "Guess"))){
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    bb.add(1);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    gyroscope.unregister();
                } else if(tx<-2.0f && ty<-2.0f && (curr_state.state.equals(  "Guess"))){
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                    bb.add(3);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    gyroscope.unregister();
                }

            }
        });
        //gyroscope.unregister();
    }


    class buttonHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            simon_motion.this.update();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    public void initGame(View v)
    {

        restart(v);
        gameSequence = new ArrayList<Integer>();
        bb = new ArrayList<Integer>();
        mMoveDelay = 750;
        playSeqCounter = 0;
        colorTouched = 0;
        scoreCounter = 0;
        curr_state.state = "Start";
        update();
    }

    private void update()
    {
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        //Generate Sequence State
        if (curr_state.state.equals("Start")) {
            clearPlayerSequence();
            generateSequence();
            playSeqCounter = 0; //reset the player sequence counter
            curr_state.state = "Play";
            inputButton.setEnabled(false);

        }

        //Play Sequence State
        if (curr_state.state.equals("Play")) {
            playSequence();
            curr_state.state = "Darken";
            if (playSeqCounter == gameSequence.size()) {
                curr_state.state = "Guess";
                inputButton.setEnabled(true);
            }
        } else if ((curr_state.state.equals("Darken"))){
            darkSequence();
            curr_state.state = "Play";
        }

        //Player repeats/guesses state
        if ((curr_state.state.equals("Guess")))
        {
            darkenLastButton();

            if (colorTouched != 0) {
                playerSequence.add(colorTouched);
                colorTouched = 0; //so it doesnt add the same color again
            }

            if (bb.size() == gameSequence.size()) {
                winOrLose = compareSequence();

                if (!winOrLose) {
                    curr_state.state = "Done";
                    gameOver();
                }
                else {
                    curr_state.state = "Start";
                    scoreCounter++;
                    Score.setText("Score: " + Integer.toString(scoreCounter));
                }
            } else {
                if(!compareSequence()){
                    curr_state.state="Done";
                    gameOver();
                }
            }

        }
        //What this does is, if you're in the player guess state, remove the time delay, so the user can enter the sequence as fast as he/she desires
        if ((curr_state.state.equals(  "Guess")))
            SimonButtonHandler.sleep(mMoveDelay);
            //otherwise, use the normal delay
        else
            SimonButtonHandler.sleep(mMoveDelay);
    }

    public void restart(View view){
        Score.setText("Score: 0");
        HighScore.setVisibility(View.INVISIBLE);
        greenLit.setImageBitmap(GreenUnlit);


        redLit.setImageBitmap(RedUnlit);


        yellowLit.setImageBitmap(YellowUnlit);

        blueLit.setImageBitmap(BlueUnlit);
        gameover.setVisibility(View.INVISIBLE);
        inputButton.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.INVISIBLE);
    }

    public void tap(View view)
    {

//        setGyroscopeListener();
//        gyroscope.register();
        setAccelerometerListener();
        accelerometer.register();

    }


    private void clearPlayerSequence()
    {
        colorTouched = 0;
        bb.clear();
    }

    //This compares the two sequences, the game sequence and the player sequence and checks for equality.
    //If they aren't equal, then the player guessed wrong and loses
    private boolean compareSequence()
    {
        ListIterator<Integer> gameSeqITR = gameSequence.listIterator();

        ListIterator<Integer> playerSeqITR = bb.listIterator();


        int gameSeqPointer, playerSeqPointer;

        while (gameSeqITR.hasNext() && playerSeqITR.hasNext())
        {
            gameSeqPointer = gameSeqITR.next();
            playerSeqPointer = playerSeqITR.next();

            System.out.println(gameSeqPointer + " : " + playerSeqPointer);

            if (gameSeqPointer != playerSeqPointer)
                return false;
        }
//        if (gameSeqITR.hasNext() || playerSeqITR.hasNext()) {
//            return false;
//        }
        return true;
    }

    //if you click the quit button, goes to quit screen
    public void quitButton(View view)
    {
        finish();
    }

    //Game over state
    private void gameOver()
    {

        inputButton.setVisibility(View.INVISIBLE);
        gameover.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.VISIBLE);
        if (scoreCounter>currHighscore){
            currHighscore = scoreCounter;
            askInitial();
            Toast.makeText(this, "You have achieved a new high score!", Toast.LENGTH_LONG).show();
        }
        HighScore.setText("Your score is " + scoreCounter + "!");
        HighScore.setVisibility(View.VISIBLE);
    }

    //Generates a new portion of the sequence and adds it onto the Array List.
    private void generateSequence()
    {
        Random RNG = new Random(); //who doesn't love a little randomness :)
        gameSequence.add(RNG.nextInt(4) + 1);
        //gameSequence.add(4);

    }

    //This plays the sequence
    private void playSequence()
    {
        long time = System.currentTimeMillis();

        if (time - mLastMove > mMoveDelay) {
//            if (playSeqCounter != 0) {
//                darkenButton(playSeqCounter - 1);
//            }
            if (playSeqCounter < gameSequence.size()) {
                lightButton(playSeqCounter);
                System.out.println(playSeqCounter);
                playSeqCounter++;
            }
            mLastMove = time;
        }
    }

    private void darkSequence(){
        long time = System.currentTimeMillis();

        if (time - mLastMove > mMoveDelay) {
            if(playSeqCounter != 0){
                darkenButton(playSeqCounter -1);
            }
        }
        mLastMove = time;
    }

    //All this function does is darken the last button in the sequence.
    //I was running into a wierd bug where the last button in the sequence would remain lit, and I could figure it out, so I just made this little function
    private void darkenLastButton()
    {
        long time = System.currentTimeMillis();

        if (time - mLastMove > mMoveDelay) {
            darkenButton(playSeqCounter-1);
        }
    }

    private void lightButton(int index) {
        ImageView litImage;

        if (gameSequence.get(index).equals(GREEN)){
            litImage = (ImageView) findViewById(R.id.greenButton);
            litImage.setImageBitmap(GreenLit);
            greenSound.start();
        }
        if (gameSequence.get(index).equals(RED)){
            litImage = (ImageView) findViewById(R.id.redButton);
            litImage.setImageBitmap(RedLit);
            redSound.start();
        }
        if (gameSequence.get(index).equals(BLUE)){
            litImage = (ImageView) findViewById(R.id.blueButton);
            litImage.setImageBitmap(BlueLit);
            blueSound.start();
        }
        if (gameSequence.get(index).equals(YELLOW)){
            litImage = (ImageView) findViewById(R.id.yellowButton);
            litImage.setImageBitmap(YellowLit);
            yellowSound.start();
        }
    }


    private void darkenButton(int index) {
        ImageView litImage;

        //switch (gameSequence.get(index)) {

        if (gameSequence.get(index).equals(GREEN)){
            litImage = (ImageView) findViewById(R.id.greenButton);
            litImage.setImageBitmap(GreenUnlit);
        }
        if (gameSequence.get(index).equals(RED)){
            litImage = (ImageView) findViewById(R.id.redButton);
            litImage.setImageBitmap(RedUnlit);
        }
        if (gameSequence.get(index).equals(BLUE)){
            litImage = (ImageView) findViewById(R.id.blueButton);
            litImage.setImageBitmap(BlueUnlit);
        }
        if (gameSequence.get(index).equals(YELLOW)){
            litImage = (ImageView) findViewById(R.id.yellowButton);
            litImage.setImageBitmap(YellowUnlit);
        }
    }

    //sets the bitmaps of all the regions
    private void setBitMaps()
    {
        GreenUnlit = BitmapFactory.decodeResource(getResources(),R.drawable.green_unlit);
        GreenLit = BitmapFactory.decodeResource(getResources(),R.drawable.green_lit);
        RedUnlit = BitmapFactory.decodeResource(getResources(),R.drawable.red_unlit);
        RedLit = BitmapFactory.decodeResource(getResources(),R.drawable.red_lit);
        BlueUnlit = BitmapFactory.decodeResource(getResources(),R.drawable.blue_unlit);
        BlueLit = BitmapFactory.decodeResource(getResources(),R.drawable.blue_lit);
        YellowUnlit = BitmapFactory.decodeResource(getResources(),R.drawable.yellow_unlit);
        YellowLit = BitmapFactory.decodeResource(getResources(),R.drawable.yellow_lit);
    }

    @Override
    protected void onResume(){
        super.onResume();

        accelerometer.register();
        gyroscope.register();
    }

    @Override
    protected void onPause(){
        super.onPause();

        accelerometer.unregister();
        gyroscope.unregister();
    }

    public static boolean isStringOnlyAlphabet(String str)
    {
        return ((str != null)
                && (!str.equals(""))
                && (str.matches("^[a-zA-Z]*$")));
    }


}
