package com.example.groomver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText loginInput, passwordInput;

    private TextView registerAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_button);
        loginInput = findViewById(R.id.login_input);
        passwordInput = findViewById(R.id.password_input);
        registerAccount = findViewById(R.id.tv_dont_have_account);
    }
}