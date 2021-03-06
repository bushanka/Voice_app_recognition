package org.vosk.demo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


public class FSL_activity extends AppCompatActivity implements RecognitionListener{

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
    static private final int STATE_MIC = 4;

    private static int STATE_NAME;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
    public static AppCompatActivity activity = null;
    public String result;
    private static DialogFragment dialog;

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
    public static String form_num;




    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_s_l);


        try {
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
            form_num = getIntent().getExtras().getString("form_name");

        } catch (Exception e) {
            e.printStackTrace();
        }

        EditText editText = findViewById(R.id.text_filed2);

        editText.addTextChangedListener(new EditTextLinesLimiter(editText, 4));


        configureBackButton();
        configureTextField1();
        configureTextField2();

        TextView name = findViewById(R.id.first_name_text);
        name.setText("?????????? "+form_num);


        setUiState(STATE_START);
        activity = this;
        STATE_NAME = 0;


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
        if ((result != null)) {
            if (result.equals(save))  // ??????????????????
                voiceSave();
            if (result.equals(up))  // ??????????
                goUp(STATE_NAME);
            if (result.equals(down))  // ????????
                goDown(STATE_NAME);
            if (result.equals(cancel))  // ????????????
                callAlert();
        }
        if (STATE_NAME == 1) {
            if ((result != null)) {
                String[] words = result.split("\\s");
                if (words.length == 2) {
                    if (value.equals(words[0])) { // ????????????????
                        fillRadio(words[1]);
                    }
                }
            }

        }
        if (STATE_NAME == 2) {
            if ((result != null)) {
                String[] words = result.split("\\s");
                if (words.length == 3) {
                    if (value.equals(words[0])) { // ????????????????
                        fillCheck(words[1], words[2]);
                    }
                }
            }

        }
        if (STATE_NAME == 4) {
            if ((result != null)) {
                if (result.equals(yes)){ //????
                    voiceExit();
                }
            }
        }
        if (STATE_NAME == 4) {
            if ((result != null)) {
                if (result.equals(no)){   //??????
                    voiceBackToForm();
                }
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

    private void fillRadio(String number){
        switch (number){
            case "????????":
                RadioButton r = findViewById(R.id.radioButton);
                r.setChecked(true);
                break;
            case "??????":
                RadioButton r2 = findViewById(R.id.radioButton2);
                r2.setChecked(true);
                break;
            case "??????":
                RadioButton r3 = findViewById(R.id.radioButton3);
                r3.setChecked(true);
                break;
        }
    }


    private void fillCheck(String number, String answer){
        switch (number){
            case "????????":
                CheckBox c = findViewById(R.id.checkBox);
                if (answer.equals(yes)) {     //????
                    c.setChecked(true);
                }
                else if (answer.equals(no)){  //??????
                    c.setChecked(false);
                }
                break;
            case "??????":
                CheckBox c2 = findViewById(R.id.checkBox2);
                if (answer.equals(yes)) {   //????
                    c2.setChecked(true);
                }
                else if (answer.equals(no)){   //??????
                    c2.setChecked(false);
                }
                break;
            case "??????":
                CheckBox c3 = findViewById(R.id.checkBox3);
                if (answer.equals(yes)) {  //????
                    c3.setChecked(true);
                }
                else if (answer.equals(no)){  //??????
                    c3.setChecked(false);
                }
                break;
        }
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

    public static void voiceExit(){
//        STATE_NAME == 0;
//        startActivity(new Intent(FSL_activity.this, VoskActivity.class));
        activity.finish();
    }

    public void voiceSave(){
        finish();
//                startActivity(new Intent(FSL_activity.this, VoskActivity.class));
//                setUiState(STATE_DONE);
//                speechService.stop();
//                speechService = null;
    }

    public static void voiceBackToForm(){
        STATE_NAME = 0;
        dialog.dismiss();
    }

    private void textField1(){
        findViewById(R.id.RadioButtonGroup).setBackgroundColor(Color.parseColor("#FFFFFF"));
        EditText textField1 = findViewById(R.id.text_filed1);
        textField1.requestFocus();
        STATE_NAME = 0;
    }

    private void textField2(){
        findViewById(R.id.checkBoxLayout).setBackgroundColor(Color.parseColor("#FFFFFF"));
        EditText firstName = findViewById(R.id.text_filed2);
        firstName.requestFocus();
        STATE_NAME = 3;
    }

    private void radioField(){
        findViewById(R.id.text_filed1).clearFocus();
        findViewById(R.id.checkBoxLayout).setBackgroundColor(Color.parseColor("#FFFFFF"));
        findViewById(R.id.RadioButtonGroup).setBackgroundColor(Color.parseColor("#F0F8FF"));
        STATE_NAME = 1;
    }

    private void checkField(){
        findViewById(R.id.text_filed2).clearFocus();
        findViewById(R.id.RadioButtonGroup).setBackgroundColor(Color.parseColor("#FFFFFF"));
        findViewById(R.id.checkBoxLayout).setBackgroundColor(Color.parseColor("#F0F8FF"));
        STATE_NAME = 2;
    }

    private void goUp(int state){
        switch (state){
            case 3:
                checkField();
                break;
            case 2:
                radioField();
                break;
            case 1:
                textField1();
                break;
            case 0:
                textField2();
                break;
        }
    }

    private void goDown(int state){
        switch (state){
            case 0:
                radioField();
                break;
            case 1:
                checkField();
                break;
            case 2:
                textField2();
                break;
            case 3:
                textField1();
                break;
        }
    }

    private void callAlert(){
        STATE_NAME = 4;
        FragmentManager manager = getSupportFragmentManager();
        dialog = new MyDialogFragment();
        dialog.show(manager, "myDialog");
    }

    protected void configureBackButton() {
        Button backButton = findViewById(R.id.backMain);
        backButton.setOnClickListener(view -> callAlert());
    }

    protected void configureTextField1(){
        findViewById(R.id.text_filed1).requestFocus();
        findViewById(R.id.text_filed1).setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                textField1Behaviour();
        });
    }

    private void textField1Behaviour(){
        STATE_NAME = 0;
        findViewById(R.id.checkBoxLayout).setBackgroundColor(Color.parseColor("#FFFFFF"));
        findViewById(R.id.RadioButtonGroup).setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    protected void configureTextField2(){
        findViewById(R.id.text_filed2).setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                textField2Behaviour();
        });
    }

    private void textField2Behaviour(){
        findViewById(R.id.text_filed2).requestFocus();
        STATE_NAME = 3;
        findViewById(R.id.checkBoxLayout).setBackgroundColor(Color.parseColor("#FFFFFF"));
        findViewById(R.id.RadioButtonGroup).setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    public static class MyDialogFragment extends AppCompatDialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String title = "???? ?????????????? ?????? ?????????? ?????????? ?????? ?????????????????????";
            String button1String = "????";
            String button2String = "??????";
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);  // ??????????????????
//            builder.setMessage(message); // ??????????????????
            builder.setPositiveButton(button1String, (dialog, id) -> voiceExit());
            builder.setNegativeButton(button2String, (dialog, id) -> voiceBackToForm());
            builder.setCancelable(true);

            return builder.create();
        }
    }


    public static class EditTextLinesLimiter implements TextWatcher {
        private final EditText editText;
        private final int maxLines;
        private String lastValue = "";

        public EditTextLinesLimiter(EditText editText, int maxLines) {
            this.editText = editText;
            this.maxLines = maxLines;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            lastValue = charSequence.toString();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editText.getLineCount() > maxLines) {
                int selectionStart = editText.getSelectionStart() - 1;
                editText.setText(lastValue);
                if (selectionStart >= editText.length()) {
                    selectionStart = editText.length();
                }
                editText.setSelection(selectionStart);
            }
        }
    }

}