package com.example.connors_yu_final;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Random;

public class simon_voice extends AppCompatActivity {

    private final static int GREEN = 1;
    private final static int RED = 2;
    private final static int YELLOW = 3;
    private final static int BLUE = 4;




    private static Bitmap GreenUnlit, GreenLit;
    private static Bitmap RedUnlit, RedLit;
    private static Bitmap BlueUnlit, BlueLit;
    private static Bitmap YellowUnlit, YellowLit;

    private static TextView Score;

    private static ArrayList<Integer> gameSequence;
    private static ArrayList<Integer> playerSequence;

    private static long mLastMove;
    private static long mMoveDelay;
    private static int playSeqCounter;
    private static int colorTouched;
    private static boolean winOrLose;
    private static int scoreCounter;
    private static Button sbut;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    Button inputButton, restartButton;
    buttonHandler SimonButtonHandler = new buttonHandler();
    private String res = "";
    private ArrayList<String> speech = new ArrayList<>();
    private HashMap<String, Integer> colorToInt = new HashMap<>();
    private ArrayList<Integer> bb;
    private ImageView gameover;
    State curr_state = new State("");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simon_voice);

        colorToInt.put("RED", 2);
        colorToInt.put("GREEN", 1);
        colorToInt.put("YELLOW", 3);
        colorToInt.put("BLUE", 4);


        sbut = findViewById(R.id.sbut);
        Score = (TextView) findViewById(R.id.scoreString);
        gameover = findViewById(R.id.gameover);
        Score = (TextView) findViewById(R.id.scoreString);
        restartButton = findViewById(R.id.restartButton);
        setBitMaps();
        sbut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }

    private void promptSpeechInput() {
        System.out.println("state" + curr_state.state);
        System.out.println(curr_state.state.equals("Guess"));

        if (!curr_state.state.equals("Guess")) {
            return;
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    System.out.println("resykt" + result.get(0).toString());
                    System.out.println("to " + result.toString());

                    String[] b = result.get(0).split(" ");
                    speech = new ArrayList<>();

                    for (int i = 0; i < b.length; i++) {
                        speech.add(b[i].toUpperCase());
                    }
                    if (res.equals("red")
                    ) {
                    }


                    bb = new ArrayList<>();

                    for (int i = 0; i < speech.size(); i++) {
                        bb.add(colorToInt.get(speech.get(i)));
                        System.out.println(bb.get(i));
                    }
                }
                break;
            }


        }
        winOrLose = compareSequence();

        if (!winOrLose) {
           // CURRENT_STATE = DEAD_STATE;
            curr_state.state = "Done";
            gameOver();
        } else {
            curr_state.state = "Start";
            scoreCounter++;
            Score.setText("Score: " + Integer.toString(scoreCounter));
        }
    }

    public void initGame(View v) {
        gameover.setVisibility(View.INVISIBLE);
        sbut.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.INVISIBLE);
        gameSequence = new ArrayList<Integer>();
        bb = new ArrayList<Integer>();
        mMoveDelay = 750;
        playSeqCounter = 0;
        colorTouched = 0;
        scoreCounter = 0;
        curr_state.state = "Start";

        update();
    }

    private class State {
        String state = "";
        public State() {
        }

        public State(String state) {
            this.state = state;
        }

    }
    private void update() {
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        if (curr_state.state.equals("Start")) {
            clearPlayerSequence();
            generateSequence();
            playSeqCounter = 0;

            curr_state.state = "Play";

        }

        if (curr_state.state.equals("Play")) {
            playSequence();
          //  CURRENT_STATE = DARK_SEQ;
            curr_state.state = "Darken";
            if (playSeqCounter == gameSequence.size())
                curr_state.state = "Guess";
        } else if (curr_state.state.equals("Darken")) {
            darkSequence();
            curr_state.state = "Play";
        }

        if (curr_state.state.equals("Guess")) {
            darkenLastButton();

        }
        if (curr_state.state.equals("Guess"))
            SimonButtonHandler.sleep(mMoveDelay);
        else
            SimonButtonHandler.sleep(mMoveDelay);
    }

    public void tap(View view) {


    }

    private void clearPlayerSequence() {
        colorTouched = 0;
        bb.clear();
    }

    private boolean compareSequence() {
        ListIterator<Integer> gameSeqITR = gameSequence.listIterator();

        ArrayList<Integer> bb = new ArrayList<>();

        for (int i = 0; i < speech.size(); i++) {
            bb.add(colorToInt.get(speech.get(i)));
            System.out.println(bb.get(i));
        }

        System.out.println(bb.toString());

        ListIterator<Integer> playerSeqITR = bb.listIterator();


        Integer gameSeqPointer, playerSeqPointer;


        while (gameSeqITR.hasNext() && playerSeqITR.hasNext()) {
            gameSeqPointer = gameSeqITR.next();
            playerSeqPointer = playerSeqITR.next();
            if (playerSeqITR == null) {
                return false;
            }

            System.out.println(gameSeqPointer + " : " + playerSeqPointer);

            if (gameSeqPointer != playerSeqPointer)
                return false;
        }

        if (gameSeqITR.hasNext() || playerSeqITR.hasNext()) {
            return false;
        }

        return true;
    }

    public void quitButton(View view) {
        finish();
    }

    private void gameOver() {
        sbut.setVisibility(View.INVISIBLE);
        gameover.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.VISIBLE);
    }

    private void generateSequence() {
        Random RNG = new Random();
        gameSequence.add(RNG.nextInt(4) + 1);

    }

    private void playSequence() {
        long time = System.currentTimeMillis();

        if (time - mLastMove > mMoveDelay) {
            if (playSeqCounter < gameSequence.size()) {
                lightButton(playSeqCounter);
                System.out.println(playSeqCounter);
                playSeqCounter++;
            }
            mLastMove = time;
        }
    }

    private void darkSequence() {
        long time = System.currentTimeMillis();

        if (time - mLastMove > mMoveDelay) {
            if (playSeqCounter != 0) {
                darkenButton(playSeqCounter - 1);
            }
        }
        mLastMove = time;
    }

    private void darkenLastButton() {
        long time = System.currentTimeMillis();

        if (time - mLastMove > mMoveDelay) {
            darkenButton(playSeqCounter - 1);
        }
    }

    private void lightButton(int index) {
        ImageView litImage;

        if (gameSequence.get(index).equals(GREEN)){
            litImage = (ImageView) findViewById(R.id.greenButton);
            litImage.setImageBitmap(GreenLit);
        }
        if (gameSequence.get(index).equals(RED)){
            litImage = (ImageView) findViewById(R.id.redButton);
            litImage.setImageBitmap(RedLit);
        }
        if (gameSequence.get(index).equals(BLUE)){
            litImage = (ImageView) findViewById(R.id.blueButton);
            litImage.setImageBitmap(BlueLit);
        }
        if (gameSequence.get(index).equals(YELLOW)){
            litImage = (ImageView) findViewById(R.id.yellowButton);
            litImage.setImageBitmap(YellowLit);
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


    private void setBitMaps() {
        GreenUnlit = BitmapFactory.decodeResource(getResources(), R.drawable.green_unlit);
        GreenLit = BitmapFactory.decodeResource(getResources(), R.drawable.green_lit);
        RedUnlit = BitmapFactory.decodeResource(getResources(), R.drawable.red_unlit);
        RedLit = BitmapFactory.decodeResource(getResources(), R.drawable.red_lit);
        BlueUnlit = BitmapFactory.decodeResource(getResources(), R.drawable.blue_unlit);
        BlueLit = BitmapFactory.decodeResource(getResources(), R.drawable.blue_lit);
        YellowUnlit = BitmapFactory.decodeResource(getResources(), R.drawable.yellow_unlit);
        YellowLit = BitmapFactory.decodeResource(getResources(), R.drawable.yellow_lit);
    }

    class buttonHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            simon_voice.this.update();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

}
