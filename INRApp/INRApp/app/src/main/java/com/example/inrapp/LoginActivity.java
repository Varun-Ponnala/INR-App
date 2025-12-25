package com.example.inrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    TextView login;
    ProgressDialog pd;
    FirebaseAuth mauth;
    FirebaseAuth.AuthStateListener authStateListener;
    TextView register;
    DatabaseReference databaseref;
    TextView forgot;
    SignInButton googleb;
    private static final int RC_SIGN_IN=1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        pd = new ProgressDialog(this);
        mauth = FirebaseAuth.getInstance();
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        login = (TextView)findViewById(R.id.submit);

//        forgot = (TextView)findViewById(R.id.forgot);
        register = (TextView)findViewById(R.id.newuser);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(LoginActivity.this,Register.class);
                startActivity(in);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_login();

            }
        });

//        forgot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent f=new Intent(LoginActivity.this,forgot_pass.class);
//                startActivity(f);
//            }
//        });




    }


    void email_login() {

        pd.setMessage("Signing In....");
        String e = email.getText().toString().trim();
        String p = password.getText().toString().trim();

        if (!TextUtils.isEmpty(e) && !TextUtils.isEmpty(p)) {

            pd.show();
            mauth.signInWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent i =new Intent(LoginActivity.this,MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                        pd.dismiss();
                    }
                    else{
                        pd.dismiss();
                        Log.e("Error Login::", task.getException().toString());


                        Toast.makeText(LoginActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
        else {


            Toast.makeText(LoginActivity.this,"Invalid Email-Id or Password",Toast.LENGTH_SHORT).show();
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("M", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        Log.d("MI", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("M", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...

                        else {
                            pd.dismiss();
                            checklogin();
                        }
                    }
                });
    }

    public void checklogin(){

        final String uid=mauth.getCurrentUser().getUid();



        databaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uid)) {

                    Intent in = new Intent(LoginActivity.this, MainActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                    finish();
                }
                else {


                    Intent in = new Intent(LoginActivity.this, setup.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                    finish();

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}




