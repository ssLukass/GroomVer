package com.example.groomver.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.groomver.R;
import com.example.groomver.fragments.HomeFragment;
import com.example.groomver.interfaces.OnDataUserReceivedCallback;
import com.example.groomver.interfaces.OnSignInCallback;
import com.example.groomver.models.Auth;
import com.example.groomver.models.User;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2500;

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
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            Auth.signIn(this, email, password, new OnSignInCallback() {
                @Override
                public void onSignIn(boolean isSigned) {
                    if(isSigned){
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }
}
