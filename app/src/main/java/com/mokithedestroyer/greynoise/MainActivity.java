package com.mokithedestroyer.greynoise;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private String mFileContents;
    private Button mParseEpisodesButton;
    private Button mInterviewButton;
    private Button mMenuButton;
    private ListView mEpisodes;
    private ImageView mLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParseEpisodesButton = (Button) findViewById(R.id.btnParse);
        mInterviewButton = (Button) findViewById(R.id.btnInterviews);
        mMenuButton = (Button) findViewById(R.id.menuBtn);
        mEpisodes = (ListView) findViewById(R.id.xmlListView);
        mLogo = (ImageView) findViewById(R.id.logo);

        //Hide list
        mEpisodes.setVisibility(View.INVISIBLE);
        mMenuButton.setVisibility(View.INVISIBLE);

        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParseEpisodesButton.setVisibility(View.VISIBLE);
                mInterviewButton.setVisibility(View.VISIBLE);
                mLogo.setVisibility(View.VISIBLE);
                mEpisodes.setVisibility(View.INVISIBLE);
                mMenuButton.setVisibility(View.INVISIBLE);
            }
        });

        mInterviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mParseEpisodesButton.setVisibility(View.INVISIBLE);
                mInterviewButton.setVisibility(View.INVISIBLE);
                mLogo.setVisibility(View.INVISIBLE);
                mEpisodes.setVisibility(View.VISIBLE);
                mMenuButton.setVisibility(View.VISIBLE);

                //TODO: PARSE CODE
                ParseEpisodes parseEpisodes = new ParseEpisodes(mFileContents);
                parseEpisodes.setTypeOfList("interviews");
                parseEpisodes.process();
                final ArrayAdapter<Episodes> arrayAdapter = new ArrayAdapter<Episodes>(
                        MainActivity.this, R.layout.list_item, parseEpisodes.getEpisodes());
                mEpisodes.setAdapter(arrayAdapter);

            }
        });

        mParseEpisodesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mParseEpisodesButton.setVisibility(View.INVISIBLE);
                mInterviewButton.setVisibility(View.INVISIBLE);
                mLogo.setVisibility(View.INVISIBLE);
                mEpisodes.setVisibility(View.VISIBLE);
                mMenuButton.setVisibility(View.VISIBLE);

                //TODO: PARSE CODE
                ParseEpisodes parseEpisodes = new ParseEpisodes(mFileContents);
                parseEpisodes.setTypeOfList("episodes");
                parseEpisodes.process();
                final ArrayAdapter<Episodes> arrayAdapter = new ArrayAdapter<Episodes>(
                        MainActivity.this, R.layout.list_item, parseEpisodes.getEpisodes());
                mEpisodes.setAdapter(arrayAdapter);

            }
        });

        mEpisodes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Send this info with the intent
                Episodes audioSource = (Episodes) parent.getItemAtPosition(position);
                String episodeAudio = audioSource.getLink();
                String episodeTitle = audioSource.getTitle();


                //Intent to Episode Player
                Intent intent = new Intent(MainActivity.this, EpisodePlayer.class);
                intent.putExtra("episodeAudio", episodeAudio);
                intent.putExtra("episodeTitle", episodeTitle);
                startActivity(intent);

            }
        });

        //Initializing the Class
        DownloadData downloadData = new DownloadData();
        downloadData.execute("https://greynoi.se/feed");

    }


    //Class to download feed
    private class DownloadData extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            mFileContents = downloadXMLFile(params[0]);
            if(mFileContents == null){
                Log.d("DownloadData", "Error downloading");
            }

            return mFileContents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("DownloadData", "Error was: " + result);
        }

        private String downloadXMLFile(String urlPath){
            StringBuilder tempBuffer = new StringBuilder();

            try{
                //Setting up the connection to the url
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d("DownloadData", "The response code is " + response);
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                //Download the data
                int charRead;
                char[] inputBuffer = new char[500];
                while(true){
                    charRead = isr.read(inputBuffer);
                    if(charRead <= 0){
                        break;
                    }
                    tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
                }

                return tempBuffer.toString();

            }catch(IOException e){
                Log.d("DownloadData", "IO Exception downloading data:" + e.getMessage());
                e.printStackTrace();
            }catch(SecurityException e){
                Log.d("DownloadData", "Security Exception, needs permission" + e.getMessage());
            }
            return null;
        }
    }
}
