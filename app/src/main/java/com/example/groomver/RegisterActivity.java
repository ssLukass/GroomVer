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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseDatabase.getInstance("https://groomver-b0d6b-default-rtdb.europe-west1.firebasedatabase.app/");
        auth = FirebaseAuth.getInstance();

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
        }else if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, getString(R.string.Enter_User_Name), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(createPassword)) {
            Toast.makeText(this, getString(R.string.Enter_Create_Password), Toast.LENGTH_SHORT).show();
        } else if (!createPassword.equals(repeatPassword)) {
            Toast.makeText(this, getString(R.string.Password_uncorrect), Toast.LENGTH_SHORT).show();
        } else {
            registerUser(userName, userEmail, createPassword);
        }

    }


//    private void isUserExist(String userName, String userEmail, String createPassword){
//        DatabaseReference users = db.getReference("users");
//
//        users.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                boolean isExist = false;
//                for(DataSnapshot user : snapshot.getChildren()){
//                    User myUser = user.getValue(User.class);
//                    if(myUser.getUserEmail().equals(userEmail)){
//                        isExist = true;
//
//                        Toast.makeText(RegisterActivity.this,
//                                getString(R.string.Not_New_Email),
//                                Toast.LENGTH_SHORT).show();
//
//                        break;
//                    }
//
//                    if(myUser.getUserName().equals(userName)){
//                        isExist = true;
//
//                        Toast.makeText(RegisterActivity.this,
//                                getString(R.string.Not_new_user_name),
//                                Toast.LENGTH_SHORT).show();
//
//                        break;
//                    }
//                }
//
//                if(!isExist){
//                    createAccount(userName, userEmail, createPassword);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }


    private void registerUser(String userName, String userEmail, String userPassword){


        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String UID = auth.getUid();
                    createAccount(userName, userEmail, userPassword, UID);
                }else{
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthUserCollisionException ex){
                        Toast.makeText(RegisterActivity.this,
                                getString(R.string.Not_New_Email),
                                Toast.LENGTH_SHORT).show();
                    }catch (FirebaseAuthInvalidCredentialsException ex){
                        if(ex.getErrorCode().equals("ERROR_INVALID_EMAIL")){
                            Toast.makeText(RegisterActivity.this,
                                    getString(R.string.Invalid_Email),
                                    Toast.LENGTH_SHORT).show();
                        }else if (ex.getErrorCode().equals("ERROR_WEAK_PASSWORD")){
                            Toast.makeText(RegisterActivity.this,
                                    getString(R.string.Weak_Password),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception ex){
                        Toast.makeText(RegisterActivity.this,
                                getString(R.string.Undefined_Error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void createAccount(String userName, String userEmail, String createPassword, String UID) {
        DatabaseReference users = db.getReference("users").push();

        User user = new User(userName, createPassword, userEmail, UID);
        String key = users.getKey();
        user.setKey(key);

        users.setValue(user);

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }
}