package org.vosk.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.widget.Button;


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

public class VoskActivity extends Activity implements
        RecognitionListener {

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
    static private final int STATE_MIC = 4;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
    public String result;

    public static String up;
    public static String down;
    public static String left;
    public static String right;
    public static String choice;
    public static String value;
    public static String save;
    public static String cancel;
    public static String yes;
    public static String no;
    public static String print;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main);

        Button settingsBtn = findViewById(R.id.settings);
        settingsBtn.setOnClickListener(view -> configureSettings());

        Button formOneButton = findViewById(R.id.createFormOne);
        formOneButton.setOnClickListener(view -> configureFormOneButton());

        Button formTwoButton = findViewById(R.id.createFormTwo);
        formTwoButton.setOnClickListener(view -> configureFormTwoButton());

        if (up == null) {
            up = "??????????";
            down = "????????";
            left = "??????????";
            right = "????????????";
            choice = "??????????";
            value = "????????????????";
            save = "??????????????????";
            cancel = "????????????????";
            yes = "????";
            no = "??????";
            print = "????????????";
        }



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
//            System.out.println(result + '\n');
        } catch (ParseException e) {
            e.printStackTrace();
        }
        createFormOne();
        createFormTwo();
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
//                pause(false);
//                resultView.setText(R.string.preparing);
//                resultView.setMovementMethod(new ScrollingMovementMethod());
//                findViewById(R.id.recognize_mic).setEnabled(false);
//                findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_READY:
                recognizeMicrophone();
//                pause(false);
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
        System.out.println(message);
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



    private void configureFormOneButton() {

        Intent intent = new Intent(VoskActivity.this, FSL_activity.class);
        intent.putExtra("up", up);
        intent.putExtra("down", down);
        intent.putExtra("left", left);
        intent.putExtra("right", right);
        intent.putExtra("choice", choice);
        intent.putExtra("yes", yes);
        intent.putExtra("no", no);
        intent.putExtra("print", print);
        intent.putExtra("save", save);
        intent.putExtra("value", value);
        intent.putExtra("cancel", cancel);
        intent.putExtra("form_name", "1");
        startActivity(intent);

    }

    private void configureFormTwoButton() {

        Intent intent = new Intent(VoskActivity.this, FSL_activity.class);
        intent.putExtra("up", up);
        intent.putExtra("down", down);
        intent.putExtra("left", left);
        intent.putExtra("right", right);
        intent.putExtra("choice", choice);
        intent.putExtra("yes", yes);
        intent.putExtra("no", no);
        intent.putExtra("print", print);
        intent.putExtra("save", save);
        intent.putExtra("value", value);
        intent.putExtra("cancel", cancel);
        intent.putExtra("form_name", "2");
        startActivity(intent);

    }

    private void configureSettings(){
        Intent intent = new Intent(VoskActivity.this, Settings.class);
        intent.putExtra("up", up);
        intent.putExtra("down", down);
        intent.putExtra("left", left);
        intent.putExtra("right", right);
        intent.putExtra("choice", choice);
        intent.putExtra("yes", yes);
        intent.putExtra("no", no);
        intent.putExtra("print", print);
        intent.putExtra("save", save);
        intent.putExtra("value", value);
        intent.putExtra("cancel", cancel);
        startActivity(intent);
    }

    private void createFormOne() {
        if ((result != null))
            if (result.equals("?????????? ????????")) {
                Intent intent = new Intent(VoskActivity.this, FSL_activity.class);
                intent.putExtra("up", up);
                intent.putExtra("down", down);
                intent.putExtra("left", left);
                intent.putExtra("right", right);
                intent.putExtra("choice", choice);
                intent.putExtra("yes", yes);
                intent.putExtra("no", no);
                intent.putExtra("print", print);
                intent.putExtra("save", save);
                intent.putExtra("value", value);
                intent.putExtra("cancel", cancel);
                intent.putExtra("form_name", "1");
                startActivity(intent);

//                setUiState(STATE_DONE);
//                speechService.stop();
//                speechService = null;
//                pause(true);
            }
    }

    private void createFormTwo() {
        if ((result != null))
            if (result.equals("?????????? ??????")) {
                Intent intent = new Intent(VoskActivity.this, FSL_activity.class);
                intent.putExtra("up", up);
                intent.putExtra("down", down);
                intent.putExtra("left", left);
                intent.putExtra("right", right);
                intent.putExtra("choice", choice);
                intent.putExtra("yes", yes);
                intent.putExtra("no", no);
                intent.putExtra("print", print);
                intent.putExtra("save", save);
                intent.putExtra("value", value);
                intent.putExtra("cancel", cancel);
                intent.putExtra("form_name", "2");
                startActivity(intent);
            }
    }

}

