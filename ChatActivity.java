package edu.cmu.pocketsphinx.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.cmu.pocketsphinx.Hypothesis;

public class ChatActivity extends AppCompatActivity {
    //note we have to add some lines on android manifest si android = 11
    private TextToSpeech textToSpeech;
    private VoiceRecognitionManager voiceRecognitionManager;
    private Button startListeningButton;
    private boolean isRecording = false;
    Button logoutButton;

    private RecognitionListener recognitionListener;
    private DatabaseHelper dbHelper;

    public void setRecognitionListener(RecognitionListener listener) {
        this.recognitionListener = listener;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        logoutButton=(Button) findViewById(R.id.logoutButton);
        startListeningButton = findViewById(R.id.startRecordingButton);
        dbHelper = new DatabaseHelper(this);
        voiceRecognitionManager = new VoiceRecognitionManager(this);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);

                    if (  result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                        Log.e("tts", "language data is missing of langauge not supported");
                            }else{
                        Log.i("tts", "lang supported and data is NOT missing");//TODO fix this
                    }
                } else {
                    Log.e("tts", "tts not init");
                }
            }
        });
        voiceRecognitionManager.setRecognitionListener(new VoiceRecognitionManager.RecognitionListener() {
            @Override
            public void onResult(Hypothesis hypothesis) {
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
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.close();
                SharedPreferences sharedPref = getSharedPreferences("preference", Context.MODE_PRIVATE); //to supp all login data
                sharedPref.edit().clear().commit();

                Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void speakText( String text) {
        //TODO to fix later par rapp au texte param DONE ?
        if(text!=null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (voiceRecognitionManager != null) {
            voiceRecognitionManager.stopListening();
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
    private void processVoiceCommand(String command) throws IOException {
        if (command.contains("joke")) {
            String joke = dbHelper.getJoke();

            if (joke != null) {
               speakText(joke);
            } else {

               speakText("I'm sorry, I couldn't find a joke.");
            }
        } else if (command.contains("study")) {
            String studyInfo = dbHelper.getCurrentStudySubject();

            if (studyInfo != null) {
                speakText("You have " + studyInfo);
            } else {

                speakText("I'm sorry, I couldn't find your current study information.");
            }
        } else if (command.contains("time")) {
            String currentTime = getCurrentTime();
            speakText("The current time is " + currentTime);

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

