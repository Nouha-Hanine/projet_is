package edu.cmu.pocketsphinx.demo;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class VoiceRecognitionManager {

    private Context context;
    private SpeechRecognizer recognizer;
    private static RecognitionListener recognitionListener;



    public void setRecognitionListener(RecognitionListener listener) {
        this.recognitionListener = listener;
    }
    public VoiceRecognitionManager(Context context) {
        this.context = context;
        initRecognizer();
    }


    private void initRecognizer() {
        try {

            File acousticModelDir = new File(context.getFilesDir(), "acoustic_model");
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
            String[] assets = context.getAssets().list("");
            if (assets != null) {
                for (String asset : assets) {
                    copyFileOrDir(asset, targetDir);
                }
            }
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
                copyAssetFile(context, sourcePath, targetPath);
            } else {
                // Répertoire
                File newDir = new File(targetPath);
                newDir.mkdirs();
                copyFileOrDir(sourcePath, newDir);
            }
        }
    }

    private void copyAssetFile(Context context, String assetFilePath, String destinationFilePath) throws IOException {
        InputStream inputStream = context.getAssets().open(assetFilePath);
        OutputStream outputStream = new FileOutputStream(destinationFilePath);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
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

    public static class RecognitionListener implements edu.cmu.pocketsphinx.RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
            
        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onPartialResult(Hypothesis hypothesis) {

        }

        public void onResult(Hypothesis hypothesis) {
            if (hypothesis != null) {
                String spokenText = hypothesis.getHypstr();

                if (recognitionListener != null) {
                    recognitionListener.onResult(spokenText);
                }
            }
        }

       private void onResult(String spokenText) {
           System.out.println("Résultat de la reconnaissance vocale : " + spokenText);
        }

        @Override
        public void onError(Exception e) {

        }

        @Override
        public void onTimeout() {

        }

    }


}




