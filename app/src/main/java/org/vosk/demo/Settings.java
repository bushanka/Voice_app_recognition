package org.vosk.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;


public class Settings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        findViewById(R.id.Approve).setOnClickListener(view -> approveSettings());
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//
//        EditText nameView = (EditText) findViewById(R.id.up_name);
//        outState.putString("up", nameView.getText().toString());
//
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        String up = savedInstanceState.getString("up");
//        EditText editText = (EditText)findViewById(R.id.up_name);
//        editText.setText(up, TextView.BufferType.EDITABLE);
//    }

    private void approveSettings(){
        EditText up = (EditText) findViewById(R.id.up_name);
        EditText down = (EditText) findViewById(R.id.down_name);
        EditText left = (EditText) findViewById(R.id.left_name);
        EditText right = (EditText) findViewById(R.id.right_name);
        EditText choice = (EditText) findViewById(R.id.choice_name);
        EditText value = (EditText) findViewById(R.id.value_name);
        EditText save = (EditText) findViewById(R.id.save_name);
        EditText cancel = (EditText) findViewById(R.id.cancel_name);
        EditText yes = (EditText) findViewById(R.id.yes_name);
        EditText no = (EditText) findViewById(R.id.no_name);
        EditText print = (EditText) findViewById(R.id.print_name);
        VoskActivity.up = up.getText().toString();
        VoskActivity.down = down.getText().toString();
        VoskActivity.left = left.getText().toString();
        VoskActivity.right = right.getText().toString();
        VoskActivity.choice = choice.getText().toString();
        VoskActivity.value = value.getText().toString();
        VoskActivity.cancel = cancel.getText().toString();
        VoskActivity.save = save.getText().toString();
        VoskActivity.yes = yes.getText().toString();
        VoskActivity.no = no.getText().toString();
        VoskActivity.print = print.getText().toString();
        finish();
    }
}