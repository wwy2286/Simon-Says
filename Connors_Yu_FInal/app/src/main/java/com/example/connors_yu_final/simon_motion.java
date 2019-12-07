package com.example.connors_yu_final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
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

    //the flow of the game should be
    //generate the sequence, play the sequence for the player, player plays it back
    private static final int GENERATE_SEQ = 1;
    private static final int PLAY_SEQ = 2;
    private static final int PLAYER_GUESS = 3;
    private static final int DEAD_STATE = 4;
    private static int CURRENT_STATE = 0;
    private static final int DARK_SEQ = 5;

    private static Bitmap GreenUnlit, GreenLit;
    private static Bitmap RedUnlit, RedLit;
    private static Bitmap BlueUnlit, BlueLit;
    private static Bitmap YellowUnlit, YellowLit;
    private ImageView gameover;
    Button inputButton, restartButton;

    private static TextView Score;

    //the two arrays that will dictate the game
    private static ArrayList<Integer> gameSequence;
    private static ArrayList<Integer> playerSequence;

    private static long mLastMove;
    private static long mMoveDelay;
    private static int playSeqCounter;
    private static int colorTouched;
    private static boolean winOrLose;
    private static int scoreCounter;


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
        //gyroscope.unregister();
        //setGyroscopeListener();
        setBitMaps();
        //setAccelerometerListener();





//


        //gyroscope.unregister();

    //initGame();
    //gyroscope.unregister();

    }

    public void setAccelerometerListener(){
        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {

                Toast.makeText(context, "x = " + tx + " y = " + ty + " z = " + tz , Toast.LENGTH_LONG);
                if(tx>1.0f && ty>1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    bb.add(2);

                    accelerometer.unregister();
                }
                else if(tx>1.0f && ty<-1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    bb.add(4);
                    accelerometer.unregister();
                }
                else if(tx<-1.0f && ty>1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    bb.add(1);

                    accelerometer.unregister();
                } else if(tx<-1.0f && ty<-1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    bb.add(3);
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
                if(tx>2.0f && ty>2.0f && CURRENT_STATE == PLAYER_GUESS){
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                    bb.add(2);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    gyroscope.unregister();
                }
                else if(tx>2.0f && ty<-2.0f && CURRENT_STATE == PLAYER_GUESS){
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                    bb.add(4);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    gyroscope.unregister();
                }
                else if(tx<-2.0f && ty>2.0f && CURRENT_STATE == PLAYER_GUESS){
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    bb.add(1);
                    System.out.println("x = " + tx + " y = " + ty + " z = " + tz);
                    gyroscope.unregister();
                } else if(tx<-2.0f && ty<-2.0f && CURRENT_STATE == PLAYER_GUESS){
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
        gameover.setVisibility(View.INVISIBLE);
        inputButton.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.INVISIBLE);
        gameSequence = new ArrayList<Integer>();
        bb = new ArrayList<Integer>();
        mMoveDelay = 750;
        playSeqCounter = 0;
        colorTouched = 0;
        scoreCounter = 0;
        CURRENT_STATE = GENERATE_SEQ;
        update();
    }

    private void update()
    {
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        //Generate Sequence State
        if (CURRENT_STATE == GENERATE_SEQ) {
            clearPlayerSequence();
            generateSequence();
            playSeqCounter = 0; //reset the player sequence counter
            CURRENT_STATE = PLAY_SEQ;

        }

        //Play Sequence State
        if (CURRENT_STATE == PLAY_SEQ) {
            playSequence();
            CURRENT_STATE = DARK_SEQ;
            if (playSeqCounter == gameSequence.size())
                CURRENT_STATE = PLAYER_GUESS;
        } else if (CURRENT_STATE == DARK_SEQ){
            darkSequence();
            CURRENT_STATE = PLAY_SEQ;
        }

        //Player repeats/guesses state
        if (CURRENT_STATE == PLAYER_GUESS)
        {
            darkenLastButton();

            if (colorTouched != 0) {
                playerSequence.add(colorTouched);
                colorTouched = 0; //so it doesnt add the same color again
            }

            if (bb.size() == gameSequence.size()) {
                winOrLose = compareSequence();

                if (!winOrLose) {
                    CURRENT_STATE = DEAD_STATE;
                    gameOver();
                }
                else {
                    CURRENT_STATE = GENERATE_SEQ;
                    scoreCounter++;
                    Score.setText("Score: " + Integer.toString(scoreCounter));
                }
            }

        }
        //What this does is, if you're in the player guess state, remove the time delay, so the user can enter the sequence as fast as he/she desires
        if (CURRENT_STATE == PLAYER_GUESS)
            SimonButtonHandler.sleep(mMoveDelay);
            //otherwise, use the normal delay
        else
            SimonButtonHandler.sleep(mMoveDelay);
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
//        Intent intent = new Intent(this, GameOver.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("scoreCounter",scoreCounter);
//        startActivity(intent);
        inputButton.setVisibility(View.INVISIBLE);
        gameover.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.VISIBLE);
    }

    //Generates a new portion of the sequence and adds it onto the Array List.
    private void generateSequence()
    {
        Random RNG = new Random(); //who doesn't love a little randomness :)
        gameSequence.add(RNG.nextInt(3) + 1);
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

    //Lightens the given button index
    private void lightButton(int index)
    {
        ImageView litImage;

        switch (gameSequence.get(index)) {
            case GREEN:
                litImage = (ImageView) findViewById(R.id.greenButton);
                litImage.setImageBitmap(GreenLit);
                System.out.println("GREEN");
                break;
            case RED:
                litImage = (ImageView) findViewById(R.id.redButton);
                litImage.setImageBitmap(RedLit);
                System.out.println("RED");
                break;
            case BLUE:
                litImage = (ImageView) findViewById(R.id.blueButton);
                litImage.setImageBitmap(BlueLit);
                System.out.println("BLUE");
                break;
            case YELLOW:
                litImage = (ImageView) findViewById(R.id.yellowButton);
                litImage.setImageBitmap(YellowLit);
                System.out.println("YELLOW");
                break;
        }
    }

    //Darkens the given button index
    private void darkenButton(int index)
    {
        ImageView litImage;

        switch (gameSequence.get(index)) {
            case GREEN:
                litImage = (ImageView) findViewById(R.id.greenButton);
                litImage.setImageBitmap(GreenUnlit);
                break;
            case RED:
                litImage = (ImageView) findViewById(R.id.redButton);
                litImage.setImageBitmap(RedUnlit);
                break;
            case BLUE:
                litImage = (ImageView) findViewById(R.id.blueButton);
                litImage.setImageBitmap(BlueUnlit);
                break;
            case YELLOW:
                litImage = (ImageView) findViewById(R.id.yellowButton);
                litImage.setImageBitmap(YellowUnlit);
                break;
        }
    }

    //sets the bitmaps of all the regions
    private void setBitMaps()
    {
        GreenUnlit = BitmapFactory.decodeResource(getResources(),R.drawable.green_unlit);
        GreenLit = BitmapFactory.decodeResource(getResources(),R.drawable.green_lit);
        RedUnlit = BitmapFactory.decodeResource(getResources(),R.drawable.red_unlit);
        RedLit = BitmapFactory.decodeResource(getResources(),R.drawable.red_lit);
        BlueUnlit = BitmapFactory.decodeResource(getResources(),R.drawable.blue_lit);
        BlueLit = BitmapFactory.decodeResource(getResources(),R.drawable.blue_unlit);
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


}
