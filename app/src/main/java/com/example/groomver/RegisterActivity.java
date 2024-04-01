package com.example.groomver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private Button registerButton;
    private EditText enterNumberPhone, nameInput, createPassword, repeatPassword;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = findViewById(R.id.register_button);
        enterNumberPhone = findViewById(R.id.enter_number_phone);
        nameInput = findViewById(R.id.name_input);
        createPassword = findViewById(R.id.create_password);
        repeatPassword = findViewById(R.id.repeat_password);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });

    }

    private void CreateAccount() {
        String number_phone = enterNumberPhone.getText().toString();
        String user_name = nameInput.getText().toString();
        String create_password = createPassword.getText().toString();
        String repeat_password = repeatPassword.getText().toString();



        if (TextUtils.isEmpty(number_phone)) {
            Toast.makeText(this, "Заполните 'Номер телефона'", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(user_name)) {
            Toast.makeText(this, "Заполните 'Имя пользователя'", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(create_password)) {
            Toast.makeText(this, "Заполните 'Придумайте пароль'", Toast.LENGTH_SHORT).show();
        } else if (!create_password.equals(repeat_password)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        } else {

            ValidatePhone(user_name,number_phone,create_password);
        }

    }

    private void ValidatePhone(String userName, String numberPhone, String createPassword) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final DatabaseReference RootRef;

        RootRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://groomver-b0d6b-default-rtdb.europe-west1.firebasedatabase.app/");

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Users").child(numberPhone).exists())){

                    HashMap<String,Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone",numberPhone);
                    userDataMap.put("login",userName);
                    userDataMap.put("password",createPassword);
                    RootRef.child("Users").child(numberPhone).updateChildren(userDataMap)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, R.string.Create_accout_complite,Toast.LENGTH_SHORT).show();

                                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(loginIntent);
                                }else{
                                    Toast.makeText(RegisterActivity.this, R.string.Error,Toast.LENGTH_SHORT).show();

                                }
                            }

                            );}
                else{
                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(loginIntent);

                    /*builder.setTitle(R.string.Alert_create_account);
                    builder.setMessage(R.string.Alert_please_wait);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    View rootView = dialog.getWindow().getDecorView().getRootView();
                    Animation animation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_out);
                    rootView.startAnimation(animation);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 2000);*/
                    Toast.makeText(RegisterActivity.this, R.string.This_number_registered, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}