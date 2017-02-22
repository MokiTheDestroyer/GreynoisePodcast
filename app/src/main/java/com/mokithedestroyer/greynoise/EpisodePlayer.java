package com.mokithedestroyer.greynoise;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import java.util.concurrent.TimeUnit;


import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class EpisodePlayer extends AppCompatActivity {

    private Button mStopPlayButton;
    private Button mBackButton;
    private TextView mEpisodeTitle;
    private TextView mEpisodeNum;
    private TextView mEpisodeLengthDisplay;
    private TextView mEpisodeTotalLengthDisplay;
    private SeekBar mSeekBar;

    private double timeElapsed = 0, finalTime = 0;
    private android.os.Handler durationHandler = new android.os.Handler();
    private String timeDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_player);

        //Episode Info
        Intent episodeInfo = getIntent();
        String episodeAudio = episodeInfo.getStringExtra("episodeAudio");
        String episodeTitle = episodeInfo.getStringExtra("episodeTitle");

        //Initialize
        mStopPlayButton = (Button) findViewById(R.id.stopPLayButton);
        mBackButton = (Button) findViewById(R.id.backButton);
        mEpisodeTitle = (TextView) findViewById(R.id.episodeTitle);
        mEpisodeNum = (TextView) findViewById(R.id.episodeNum);
        mEpisodeLengthDisplay = (TextView) findViewById(R.id.episodeLengthDisplay);
        mEpisodeTotalLengthDisplay = (TextView) findViewById(R.id.episodeTotalLengthDisplay);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);

        //Episode Display
        if(episodeTitle.substring(0, 7).toLowerCase().contains("episode")) {
            //Split the Episode and Title
            String[] splitEpisodeContent = episodeTitle.split(" - ");
            String splitEpisodeNum = splitEpisodeContent[0];
            String splitEpisodeTitle = splitEpisodeContent[1];


            //Episode Title display
            mEpisodeNum.setText(splitEpisodeNum);
            mEpisodeTitle.setText(splitEpisodeTitle);
        }else{
            mEpisodeNum.setVisibility(View.GONE);
            mEpisodeTitle.setText(episodeTitle);
        }

        //Media Player
        final MediaPlayer player = new MediaPlayer();

        final Runnable updateSeekBarTime = new Runnable() {
            public void run() {
                //get current position
                timeElapsed = player.getCurrentPosition();
                //set seekbar progress
                mSeekBar.setProgress((int) timeElapsed);
                //set time remaining
                double timeRemaining = finalTime + timeElapsed;
                timeDisplay = (String.format("%02d : %02d", MILLISECONDS.toMinutes((long) timeRemaining), MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes((long) timeRemaining))));

                mEpisodeLengthDisplay.setText(timeDisplay);

                //repeat yourself that again in 100 miliseconds
                durationHandler.postDelayed(this, 100);
            }
        };

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(episodeAudio);
            player.prepare();
            player.start();

            timeElapsed = player.getCurrentPosition();
            mSeekBar.setProgress((int) timeElapsed);
            durationHandler.postDelayed(updateSeekBarTime, 100);

        } catch (Exception e) {
            // TODO: handle exception
        }

        int episodeDuration = player.getDuration();

        //Play and Pause button
        mStopPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(player.isPlaying()){
                    player.pause();
                }else{
                    player.start();
                }
            }
        });


        //Set Final Total time display
        String totalTime = (String.format("%02d : %02d", MILLISECONDS.toMinutes((long) episodeDuration), MILLISECONDS.toSeconds((long) episodeDuration) - TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes((long) episodeDuration))));
        mEpisodeTotalLengthDisplay.setText(totalTime);

        //Seek Bar Set up

        mSeekBar.setMax(episodeDuration);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser){
                    int seekPos = mSeekBar.getProgress();
                    player.seekTo(seekPos);
                    mEpisodeLengthDisplay.setText(timeDisplay);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        //Back to episodes button
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                Intent intent = new Intent(EpisodePlayer.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

}

