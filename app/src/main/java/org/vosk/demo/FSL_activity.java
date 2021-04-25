package org.vosk.demo;

import android.os.Bundle;
import android.widget.Button;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.IOException;
import androidx.appcompat.app.AppCompatActivity;



public class FSL_activity extends AppCompatActivity implements RecognitionListener{

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
    //    static private final int STATE_FILE = 3;
    static private final int STATE_MIC = 4;

    private int STATE_NAME = -1;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
//    private TextView resultView;
    public String result;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_s_l);


        configureBackButton();

        setUiState(STATE_START);

//        findViewById(R.id.recognize_mic).setOnClickListener(view -> recognizeMicrophone());
//        recognizeMicrophone();
//        ((ToggleButton) findViewById(R.id.pause)).setOnCheckedChangeListener((view, isChecked) -> pause(isChecked));

        LibVosk.setLogLevel(LogLevel.INFO);

        // Check if user has given permission to record audio, init the model after permission is granted
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            initModel();
        }
    }

    private void initModel() {
        StorageService.unpack(this, "model-ru", "model",
                (model) -> {
                    this.model = model;
                    setUiState(STATE_READY);
                },
                (exception) -> setErrorState("Failed to unpack the model" + exception.getMessage()));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                initModel();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (speechService != null) {
            speechService.stop();
            speechService.shutdown();
        }

        if (speechStreamService != null) {
            speechStreamService.stop();
        }
    }

    @Override
    public void onResult(String hypothesis) {
        try {
            Object obj = new JSONParser().parse(hypothesis);
            JSONObject jo = (JSONObject) obj;
            result = (String) jo.get("text");
            System.out.println(result + '\n');
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ((result != null)) {
            switch (result){
                case "назад":
                    voiceBack();
                    break;
                case "имя":
                    firstName();
                    break;
                case "фамилия":
                    secondName();
                    break;
                case "отчество":
                    lastName();
                    break;
                case "вверх":
                    goUp(STATE_NAME);
                    break;
                case "вниз":
                    goDown(STATE_NAME);
                    break;
            }
        }
    }

    @Override
    public void onFinalResult(String hypothesis) {
        setUiState(STATE_DONE);
        if (speechStreamService != null) {
            speechStreamService = null;
        }
    }

    @Override
    public void onPartialResult(String hypothesis) {
    }

    @Override
    public void onError(Exception e) {
        setErrorState(e.getMessage());
    }

    @Override
    public void onTimeout() {
        setUiState(STATE_DONE);
    }

    private void setUiState(int state) {
        switch (state) {
            case STATE_START:
//                resultView.setText(R.string.preparing);
//                resultView.setMovementMethod(new ScrollingMovementMethod());
//                findViewById(R.id.recognize_mic).setEnabled(false);
//                findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_READY:
                recognizeMicrophone();
//                resultView.setText(R.string.ready);
//                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
//                findViewById(R.id.recognize_mic).setEnabled(true);
//                findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_DONE:
//                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
//                findViewById(R.id.recognize_mic).setEnabled(true);
//                findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_MIC:
//                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.stop_microphone);
//                findViewById(R.id.recognize_mic).setEnabled(true);
//                findViewById(R.id.pause).setEnabled((true));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    private void setErrorState(String message) {
//        System.out.println(message);
//        resultView.setText(message);
//        ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
//        findViewById(R.id.recognize_mic).setEnabled(false);
    }


    private void recognizeMicrophone() {
        if (speechService != null) {
            setUiState(STATE_DONE);
            speechService.stop();
            speechService = null;
        } else {
            setUiState(STATE_MIC);
            try {
                Recognizer rec = new Recognizer(model, 16000.0f);
                speechService = new SpeechService(rec, 16000.0f);
                speechService.startListening(this);
            } catch (IOException e) {
                setErrorState(e.getMessage());
            }
        }
    }

    private void voiceBack(){
        finish();
//                startActivity(new Intent(FSL_activity.this, VoskActivity.class));
//                setUiState(STATE_DONE);
//                speechService.stop();
//                speechService = null;
    }

    private void firstName(){
        EditText firstName = (EditText) findViewById(R.id.first_name);
        firstName.requestFocus();
        STATE_NAME = 0;
    }

    private void secondName(){
        EditText secondName = (EditText) findViewById(R.id.second_name);
        secondName.requestFocus();
        STATE_NAME = 1;
    }

    private void lastName(){
        EditText lastName = (EditText) findViewById(R.id.last_name);
        lastName.requestFocus();
        STATE_NAME = 2;
    }

    private void goUp(int state){
        switch (state){
            case 1:
                firstName();
                break;
            case 2:
                secondName();
                break;
            case 0:
            case -1:
                break;
        }
    }

    private void goDown(int state){
        switch (state){
            case 0:
                secondName();
                break;
            case 1:
                lastName();
                break;
            case 2:
            case -1:
                break;
        }
    }

    protected void configureBackButton() {
        Button backButton = findViewById(R.id.backMain);
        backButton.setOnClickListener(view -> finish());
    }

}