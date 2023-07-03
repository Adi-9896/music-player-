package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class playsong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        UpdateSeekbar.interrupt();
    }
    TextView textView;
    ImageView play,previous,next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textcontent;
    int positions;
    SeekBar seekBar;
   Thread UpdateSeekbar;
    Handler handler;
    Runnable runnable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);
        textView=findViewById(R.id.textView);
        play = findViewById(R.id.play);
        next= findViewById(R.id.next);
        seekBar =findViewById(R.id.seek);
        previous = findViewById(R.id.previous);

        Intent intent = getIntent();
        Bundle bundle  =  intent.getExtras();
        songs =(ArrayList)bundle.getParcelableArrayList("songlist");
        textcontent = intent.getStringExtra("currentsongs");
        textView.setText(textcontent);
        textView.setSelected(true);
        positions = intent.getIntExtra("position",0);
        Uri uri = Uri.parse(songs.get(positions).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               if (fromUser){//TO FORWARD MUSIC USING SEEKBAR
                  mediaPlayer.seekTo(progress);
                  seekBar.setProgress(progress);
               }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });//TO MOVE SEEKBAR ALONG WITH SONGS
        UpdateSeekbar = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try{
                    while(currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(1000);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        UpdateSeekbar.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                } else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (positions!=0){
                    positions = positions-1;
                }
                else {
                    positions =songs.size()-1;
                }
                Uri uri = Uri.parse(songs.get(positions).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textcontent = songs.get(positions).toString();
                textView.setText(textcontent);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (positions!=songs.size()-1){
                    positions = positions+1;
                }
                else {
                    positions = 0;
                }
                Uri uri = Uri.parse(songs.get(positions).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textcontent = songs.get(positions).toString();
                textView.setText(textcontent);
            }
        });

    }
}

