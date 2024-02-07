package com.example.saqsi;

import androidx.appcompat.app.AppCompatActivity;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import android.media.MediaPlayer;
import android.net.Uri;

import android.os.Bundle;
import android.widget.Button;
import android.view.View;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements VoiceRecognitionManager.RecognitionListener {


    private VoiceRecognitionManager voiceRecognitionManager;
    private Button startListeningButton;
    private boolean isRecording = false;

    private RecognitionListener recognitionListener;
    private DatabaseHelper dbHelper;

    public void setRecognitionListener(RecognitionListener listener) {
        this.recognitionListener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        startListeningButton = findViewById(R.id.startRecordingButton);
        dbHelper = new DatabaseHelper(this);
        voiceRecognitionManager = new VoiceRecognitionManager(this);

        startListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {

                    isRecording = false;
                    voiceRecognitionManager.stopListening();
                } else {

                    isRecording = true;
                    voiceRecognitionManager.startListening();
                }
            }
        });






        voiceRecognitionManager.setRecognitionListener(new VoiceRecognitionManager.RecognitionListener() {
            @Override
            public void onResult(String hypothesis) {
                if (hypothesis != null) {
                    String spokenText = hypothesis.getHypstr();
                    if (isRecording) {
                        try {
                            processVoiceCommand(spokenText);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


        });


    }

    @Override
    protected void onDestroy() {
        if (voiceRecognitionManager != null) {
            voiceRecognitionManager.stopListening();
        }
        super.onDestroy();
    }


    private void processVoiceCommand(String command) throws IOException {
        if (command.contains("joke")) {
            String joke = dbHelper.getJoke();

            if (joke != null) {
                voiceRecognitionManager.speak(joke);
            } else {

                voiceRecognitionManager.speak("I'm sorry, I couldn't find a joke.");
            }
        } else if (command.contains("study")) {
            String studyInfo = dbHelper.getCurrentStudySubject();

            if (studyInfo != null) {
                voiceRecognitionManager.speak("You have " + studyInfo);
            } else {

                voiceRecognitionManager.speak("I'm sorry, I couldn't find your current study information.");
            }
        } else if (command.contains("time")) {
            String currentTime = getCurrentTime();

            voiceRecognitionManager.speak("The current time is " + currentTime);
        } else if (command.contains("music")) {
            playMusic();
        }
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void playMusic() {

        String pathToYourMusicFile = "path_to_music";


        final MediaPlayer[] mediaPlayer = {new MediaPlayer()};

        try {

            mediaPlayer[0].setDataSource(this, Uri.parse(pathToYourMusicFile));


            mediaPlayer[0].prepare();


            mediaPlayer[0].start();


            mediaPlayer[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mediaPlayer[0] != null && mediaPlayer[0].isPlaying()) {
                        mediaPlayer[0].stop();
                        mediaPlayer[0].release();
                        mediaPlayer[0] = null;
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

