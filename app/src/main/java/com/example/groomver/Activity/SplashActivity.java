package com.example.groomver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.groomver.R;
import com.example.groomver.fragments.HomeFragment;
import com.example.groomver.interfaces.OnDataUserReceivedCallback;
import com.example.groomver.models.Auth;
import com.example.groomver.models.User;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAuthentication();
            }
        }, SPLASH_TIME_OUT);
    }

    private void checkAuthentication() {
        FirebaseUser currentUser = Auth.getCurrentUserFBAuth();
        if (currentUser != null) {
            Auth.getDatabaseCurrentUser(new OnDataUserReceivedCallback() {
                @Override
                public void onUserReceived(User user) {
                    if (user != null) {
                        startActivity(new Intent(SplashActivity.this, HomeFragment.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                }
            });
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }
}
