package com.example.groomver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private Button registerButton;
    private EditText etEnterNumberPhone, etNameInput, etCreatePassword, etRepeatPassword;

    private FirebaseDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseDatabase.getInstance("https://authtest-f946a-default-rtdb.europe-west1.firebasedatabase.app/");

        registerButton = findViewById(R.id.register_button);
        etEnterNumberPhone = findViewById(R.id.enter_number_phone);
        etNameInput = findViewById(R.id.name_input);
        etCreatePassword = findViewById(R.id.create_password);
        etRepeatPassword = findViewById(R.id.repeat_password);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }

    private void validateData() {
        String numberPhone = etEnterNumberPhone.getText().toString();
        String userName = etNameInput.getText().toString();
        String createPassword = etCreatePassword.getText().toString();
        String repeatPassword = etRepeatPassword.getText().toString();

        if (TextUtils.isEmpty(numberPhone)) {
            Toast.makeText(this, "Заполните 'Номер телефона'", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Заполните 'Имя пользователя'", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(createPassword)) {
            Toast.makeText(this, "Заполните 'Придумайте пароль'", Toast.LENGTH_SHORT).show();
        } else if (!createPassword.equals(repeatPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
        } else {
            isUserExist(userName, numberPhone, createPassword);
        }

    }

    private void isUserExist(String userName, String numberPhone, String createPassword){
        DatabaseReference users = db.getReference("users");

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isExist = false;
                for(DataSnapshot user : snapshot.getChildren()){
                    User myUser = user.getValue(User.class);
                    if(myUser.getPhoneNumber().equals(numberPhone)){
                        isExist = true;

                        Toast.makeText(RegisterActivity.this,
                                "Пользователь с таким телефоном уже существует",
                                Toast.LENGTH_SHORT).show();

                        break;
                    }

                    if(myUser.getUserName().equals(userName)){
                        isExist = true;

                        Toast.makeText(RegisterActivity.this,
                                "Пользователь с таким именем уже существует",
                                Toast.LENGTH_SHORT).show();

                        break;
                    }
                }

                if(!isExist){
                    createAccount(userName, numberPhone, createPassword);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createAccount(String userName, String numberPhone, String createPassword) {
        DatabaseReference users = db.getReference("users").push();

        User user = new User(userName, createPassword, numberPhone);
        String key = users.getKey();
        user.setKey(key);

        users.setValue(user);

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}