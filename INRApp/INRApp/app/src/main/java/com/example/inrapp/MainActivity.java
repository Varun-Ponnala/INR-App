package com.example.inrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mauth;
    TextView name;
    DatabaseReference databaseref;
    String mname;
    ActionBar actionBar;
//    Firebase m;
    FirebaseAuth.AuthStateListener authStateListener;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mauth= FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){

                    Intent i =new Intent(MainActivity.this,LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
                else{
                    bottomNavigationView = findViewById(R.id.bottom_navigation);
                    bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
                    loadFragment(new Home());
                }
            }
        };

        mauth.addAuthStateListener(authStateListener);


    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment;
            switch (item.getItemId()){
                case R.id.home:
                    fragment = new Home();

                    loadFragment(fragment);
                    return true;
                case R.id.calculator:
                    fragment = new DoceCalciFragment();

                    loadFragment(fragment);
                    return true;
                case R.id.profile:
                    fragment = new Profile();

                    loadFragment(fragment);
                    return true;
                case R.id.info:
                    fragment = new Diet_Stratergy();

                    loadFragment(fragment);
                    return true;
                case R.id.aboutus:
                    fragment = new about_us();

                    loadFragment(fragment);
                    return true;
            }
            return false;
        };
    };

    private void loadFragment(Fragment fragment){


        if(!isFinishing()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}
