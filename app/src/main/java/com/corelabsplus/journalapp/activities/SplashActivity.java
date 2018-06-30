package com.corelabsplus.journalapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.corelabsplus.journalapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;

        mAuth = FirebaseAuth.getInstance();



    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check for current user

        if (mAuth.getCurrentUser() == null){
            Intent intent = new Intent(context, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        else {
            Intent intent = new Intent(context, EntriesActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
