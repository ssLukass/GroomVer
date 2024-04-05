package com.example.groomver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.regex.Pattern;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private Button registerButton;
    private EditText etEmail, etNameInput, etCreatePassword, etRepeatPassword;

    private FirebaseDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db=FirebaseDatabase.getInstance("https://groomver-b0d6b-default-rtdb.europe-west1.firebasedatabase.app/");
        registerButton = findViewById(R.id.register_button);
        etEmail = findViewById(R.id.enter_email);
        etNameInput = findViewById(R.id.name_input);
        etCreatePassword = findViewById(R.id.create_password);
        etRepeatPassword = findViewById(R.id.repeat_password);


        registerButton.setOnClickListener(v -> validateData());

    }

    private void validateData() {
        String userEmail = etEmail.getText().toString();
        String userName = etNameInput.getText().toString();
        String createPassword = etCreatePassword.getText().toString();
        String repeatPassword = etRepeatPassword.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, getString(R.string.Enter_Email), Toast.LENGTH_SHORT).show();
        } else if (!isValidEmail(userEmail)) {
            Toast.makeText(this, getString(R.string.Invalid_Email), Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, getString(R.string.Enter_User_Name), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(createPassword)) {
            Toast.makeText(this, getString(R.string.Enter_Create_Password), Toast.LENGTH_SHORT).show();
        } else if (!createPassword.equals(repeatPassword)) {
            Toast.makeText(this, getString(R.string.Password_uncorrect), Toast.LENGTH_SHORT).show();
        } else {
            isUserExist(userName, userEmail, createPassword);
        }

    }

    private void isUserExist(String userName, String userEmail, String createPassword){
        DatabaseReference users = db.getReference("users");

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isExist = false;
                for(DataSnapshot user : snapshot.getChildren()){
                    User myUser = user.getValue(User.class);
                    if(myUser.getUserEmail().equals(userEmail)){
                        isExist = true;

                        Toast.makeText(RegisterActivity.this,
                                getString(R.string.Not_New_Email),
                                Toast.LENGTH_SHORT).show();

                        break;
                    }

                    if(myUser.getUserName().equals(userName)){
                        isExist = true;

                        Toast.makeText(RegisterActivity.this,
                                getString(R.string.Not_new_user_name),
                                Toast.LENGTH_SHORT).show();

                        break;
                    }
                }

                if(!isExist){
                    createAccount(userName, userEmail, createPassword);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createAccount(String userName, String userEmail, String createPassword) {
        DatabaseReference users = db.   getReference("users").push();

        User user = new User(userName, createPassword, userEmail);
        String key = users.getKey();
        user.setKey(key);

        /*AlertDialog.Builder builder = new AlertDialog.Builder(this );

        builder.setTitle("Создание аккаунта");
        builder.setMessage("Пожалуйста подождите...");
        builder.create();*/

        users.setValue(user);

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}