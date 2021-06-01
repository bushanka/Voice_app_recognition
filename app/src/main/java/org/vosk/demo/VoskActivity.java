package org.vosk.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.Map;

public class VoskActivity extends Activity implements
        RecognitionListener {

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
//    static private final int STATE_FILE = 3;
    static private final int STATE_MIC = 4;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
    public String result;

    String up;
    String down;
    String left;
    String right;
    String choice;
    String value;
    String save;
    String cancel;
    String yes;
    String no;
    String print;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main);

        up = getIntent().getExtras().getString("up");
        down = getIntent().getExtras().getString("down");
        left = getIntent().getExtras().getString("left");
        right = getIntent().getExtras().getString("right");
        choice = getIntent().getExtras().getString("choice");
        value = getIntent().getExtras().getString("value");
        save = getIntent().getExtras().getString("save");
        cancel = getIntent().getExtras().getString("cancel");
        yes = getIntent().getExtras().getString("yes");
        no = getIntent().getExtras().getString("no");
        print = getIntent().getExtras().getString("print");

        if (up.length() == 0 | down.length() == 0){
            up = "вверх";
            down = "вниз";
            left = "влево";
            right = "вправо";
            choice = "выбор";
            value = "значение";
            save = "сохранить";
            cancel = "отменить";
            yes = "да";
            no = "нет";
            print = "печать";

        }

//        Команды навигации по формам:
//                - Вверх
//                - Вниз
//                - Влево
//                - Вправо
//                - Выбор
//                - Значение
//
//        Общие команды:
//                - Сохранить
//                - Отменить
//                - Да
//                - Нет
//                - Печать


        configureFormOneButton();
        createFormOne();
        createFormTwo();
        configureSettings();

        // Setup layout
        // Setup layout
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
        createFormOne();
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
        Button formOneButton = findViewById(R.id.createFormOne);
        formOneButton.setOnClickListener(view -> startActivity(new Intent(VoskActivity.this, FSL_activity.class)));
    }

    private void configureSettings(){
        Button settingsBtn = findViewById(R.id.settings);
        settingsBtn.setOnClickListener(view -> startActivity(new Intent(VoskActivity.this, Settings.class)));
    }

    private void createFormOne() {
        if ((result != null))
            if (result.equals("форма один")) {
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
                startActivity(intent);

//                setUiState(STATE_DONE);
//                speechService.stop();
//                speechService = null;
//                pause(true);
            }
    }

    private void createFormTwo() {
        if ((result != null))
            if (result.equals("форма два")) {
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
                startActivity(intent);
            }
    }

    private void pause(boolean checked) {
        if (speechService != null) {
            speechService.setPause(checked);
        }
    }

}
