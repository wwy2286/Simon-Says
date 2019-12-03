package com.example.connors_yu_final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView welcome;
    Button voice_b, motion_b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcome = findViewById(R.id.welcome);
        welcome.setGravity(Gravity.CENTER);
        voice_b = findViewById(R.id.voice_b);
        motion_b = findViewById(R.id.motion_b);
        welcome.setTextColor(Color.WHITE    );
        voice_b.setTextColor(Color.WHITE);
    }

    public void voiceClick(View view)
    {
        Intent intent = new Intent(this, simon_voice.class);
        startActivity(intent);
    }

    public void motionClick(View view)
    {
        Intent intent = new Intent(this,simon_motion.class);
        startActivity(intent);
    }
}
