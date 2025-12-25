package com.example.inrapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText name_e,email_e,password;
    TextView submit;
    String uid;
    FirebaseAuth mauth;
    DatabaseReference mref;
    ProgressDialog pd;
    TextView login_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pd = new ProgressDialog(this);
        mauth= FirebaseAuth.getInstance();
//        uid=mauth.getCurrentUser().getUid();
        mref= FirebaseDatabase.getInstance().getReference();

        name_e=(EditText)findViewById(R.id.name);
        email_e=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        submit=(TextView)findViewById(R.id.submit);

        login_user = (TextView)findViewById(R.id.olduser);
        login_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Register.this,LoginActivity.class);
                startActivity(in);
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email_e.getText().toString().trim();
                String p = password.getText().toString().trim();
                if (!TextUtils.isEmpty(e) && !TextUtils.isEmpty(p)) {
                    pd.setMessage("Registering User....");
                    pd.show();
                    mauth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                pd.dismiss();
                                String uid = mauth.getCurrentUser().getUid();
                                DatabaseReference databaseref1 = mref.child(uid);
                                databaseref1.child("User Info").child("Name").setValue(name_e.getText().toString());
                                databaseref1.child("User Info").child("Email").setValue(email_e.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent i = new Intent(Register.this, WelcomeActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });


                                Log.i("Abhi", uid);


                            } else {
                                pd.dismiss();
                                Toast.makeText(Register.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                }
                else {


                    Toast.makeText(Register.this,"Invalid Email-Id or Password",Toast.LENGTH_SHORT).show();
                }

//



            }
        });

    }
}
