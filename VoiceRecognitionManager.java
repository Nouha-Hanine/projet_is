package com.example.saqsi;

import android.content.Context;
import android.os.AsyncTask;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import java.io.File;
import java.io.IOException;

public class VoiceRecognitionManager {

    private Context context;
    private SpeechRecognizer recognizer;
    private RecognitionListener recognitionListener;



    public void setRecognitionListener(RecognitionListener listener) {
        this.recognitionListener = listener;
    }
    public VoiceRecognitionManager(Context context) {
        this.context = context;
        initRecognizer();
    }


    private void initRecognizer() {
        try {

            File acousticModelDir = new File(getFilesDir(), "acoustic_model");
            if (!acousticModelDir.exists()) {

                extractAssets(acousticModelDir);
            }


            recognizer = SpeechRecognizerSetup.defaultSetup()
                    .setAcousticModel(acousticModelDir)
                    .setDictionary(new File(acousticModelDir, "dictionary"))
                    .getRecognizer();


            recognizer.addKeyphraseSearch("joke", "joke");
            recognizer.addKeyphraseSearch("study", "study");
            recognizer.addKeyphraseSearch("time", "time");
            recognizer.addKeyphraseSearch("music", "music");


            recognizer.addListener(new RecognitionListener());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractAssets(File targetDir) {
        try {

            copyFileOrDir("acoustic_model", targetDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFileOrDir(String path, File targetDir) throws IOException {
        String[] assets = context.getAssets().list(path);
        for (String asset : assets) {
            String sourcePath = path + File.separator + asset;
            String targetPath = targetDir.getAbsolutePath() + File.separator + asset;
            if (context.getAssets().list(sourcePath).length == 0) {
                // Fichier
                FileUtils.copyInputStreamToFile(context.getAssets().open(sourcePath), new File(targetPath));
            } else {
                // Répertoire
                File newDir = new File(targetPath);
                newDir.mkdirs();
                copyFileOrDir(sourcePath, newDir);
            }
        }
    }



    public void startListening() {
        if (recognizer != null) {
            recognizer.startListening("keyword_search");
        }
    }

    public void stopListening() {
        if (recognizer != null) {
            recognizer.stop();
        }
    }

    private class StartListeningTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            recognizer.startListening("keyword_search");
            return null;
        }
    }

    public class RecognitionListener implements edu.cmu.pocketsphinx.RecognitionListener {

        public void onResult(String hypothesis) {
            if (hypothesis != null) {
                String spokenText = hypothesis.getHypstr();

                if (recognitionListener != null) {
                    recognitionListener.onResult(spokenText);
                }
            }
        }

    }

    private void processVoiceCommand(String command) {

    }
}


