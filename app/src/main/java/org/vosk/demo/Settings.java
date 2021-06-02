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

        try {
            String up = getIntent().getExtras().getString("up");
            String down = getIntent().getExtras().getString("down");
            String left = getIntent().getExtras().getString("left");
            String right = getIntent().getExtras().getString("right");
            String choice = getIntent().getExtras().getString("choice");
            String value = getIntent().getExtras().getString("value");
            String save = getIntent().getExtras().getString("save");
            String cancel = getIntent().getExtras().getString("cancel");
            String yes = getIntent().getExtras().getString("yes");
            String no = getIntent().getExtras().getString("no");
            String print = getIntent().getExtras().getString("print");

            EditText upText = findViewById(R.id.up_name);
            upText.setText(up, TextView.BufferType.EDITABLE);

            EditText downText = findViewById(R.id.down_name);
            downText.setText(down, TextView.BufferType.EDITABLE);

            EditText leftText = findViewById(R.id.left_name);
            leftText.setText(left, TextView.BufferType.EDITABLE);

            EditText rightText = findViewById(R.id.right_name);
            rightText.setText(right, TextView.BufferType.EDITABLE);

            EditText valueText = findViewById(R.id.value_name);
            valueText.setText(value, TextView.BufferType.EDITABLE);

            EditText cancelText = findViewById(R.id.cancel_name);
            cancelText.setText(cancel, TextView.BufferType.EDITABLE);

            EditText saveText = findViewById(R.id.save_name);
            saveText.setText(save, TextView.BufferType.EDITABLE);

            EditText yesText = findViewById(R.id.yes_name);
            yesText.setText(yes, TextView.BufferType.EDITABLE);

            EditText noText = findViewById(R.id.no_name);
            noText.setText(no, TextView.BufferType.EDITABLE);

            EditText choiceText = findViewById(R.id.choice_name);
            choiceText.setText(choice, TextView.BufferType.EDITABLE);

            EditText printText = findViewById(R.id.print_name);
            printText.setText(print, TextView.BufferType.EDITABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void approveSettings(){
        EditText up = findViewById(R.id.up_name);
        EditText down = findViewById(R.id.down_name);
        EditText left = findViewById(R.id.left_name);
        EditText right = findViewById(R.id.right_name);
        EditText choice = findViewById(R.id.choice_name);
        EditText value = findViewById(R.id.value_name);
        EditText save = findViewById(R.id.save_name);
        EditText cancel = findViewById(R.id.cancel_name);
        EditText yes = findViewById(R.id.yes_name);
        EditText no = findViewById(R.id.no_name);
        EditText print = findViewById(R.id.print_name);
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