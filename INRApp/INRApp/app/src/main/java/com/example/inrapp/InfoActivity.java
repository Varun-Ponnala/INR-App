package com.example.inrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    TextView submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        submit = (TextView)findViewById(R.id.submit);
        TextView tv = (TextView) findViewById(R.id. website);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InfoActivity.this, DoseCalculatur.class);
                startActivity(i);
                finish();
            }
        });
    }
}
