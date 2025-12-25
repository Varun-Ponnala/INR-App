package com.example.inrapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class setup extends AppCompatActivity {
EditText sname;
    EditText smobile;
    Button sfinish;
    FirebaseAuth mauth;
DatabaseReference databaseref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        sname = (EditText)findViewById(R.id.Sname);
        smobile = (EditText)findViewById(R.id.smobile);
        sfinish = (Button)findViewById(R.id.sfinish);
        mauth= FirebaseAuth.getInstance();
        databaseref= FirebaseDatabase.getInstance().getReference().child("Users");
        sfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(sname.getText().toString()) && !TextUtils.isEmpty(smobile.getText().toString())){
                String uid = mauth.getCurrentUser().getUid();
                DatabaseReference databaseref1 = databaseref.child(uid);

                databaseref1.child("Name").setValue(sname.getText().toString());
                databaseref1.child("Mobile").setValue(smobile.getText().toString());
                    Intent in = new Intent(setup.this, MainActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                    finish();
                }



                else{

                    Toast.makeText(setup.this,"Please enter all details", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
