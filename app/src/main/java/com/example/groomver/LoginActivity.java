package com.example.groomver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText etEmail, etPassword;

    private TextView registerAccount;

    private FirebaseAuth auth;

    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance("https://groomver-b0d6b-default-rtdb.europe-west1.firebasedatabase.app/");

//        String email = etEmail.getText().toString();
//        auth.sendPasswordResetEmail(email);

        loginButton = findViewById(R.id.login_button);
        etEmail = findViewById(R.id.login_input);
        etPassword = findViewById(R.id.password_input);
        registerAccount = findViewById(R.id.tv_dont_have_account);

        registerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData() {
        String userEmail = etEmail.getText().toString();
        String userPassword = etPassword.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, getString(R.string.Enter_Email), Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, getString(R.string.Enter_Password), Toast.LENGTH_SHORT).show();
        } else {
            loginUser(userEmail, userPassword);
        }
    }

    public void updateUserInFireBase(User user){
        DatabaseReference users = db.getReference("users");
        users.child(user.getKey()).setValue(user);
    }

    public void getDatabaseCurrentUser(OnDataUserReceivedListener listener) {
        DatabaseReference users = db.getReference("users");
        FirebaseUser userFBAuth = auth.getCurrentUser();

        if (userFBAuth != null ) {
            users.orderByChild("uid").equalTo(userFBAuth.getUid()).limitToFirst(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                User user = ds.getValue(User.class);
                                listener.onUserReceived(user);
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }


    private void loginUser(String email, String password){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    getDatabaseCurrentUser(new OnDataUserReceivedListener() {
                        @Override
                        public void onUserReceived(User user) {
                            user.setPassword(password);
                            updateUserInFireBase(user);
                        }
                    });

                   Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                   startActivity(intent);
                }else{
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException ex){
                        Toast.makeText(LoginActivity.this,
                                getString(R.string.Incorrect_Email_Or_Password),
                                Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception ex){
                        Toast.makeText(LoginActivity.this,
                                getString(R.string.Undefined_Error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    interface OnDataUserReceivedListener{
        void onUserReceived(User user);
    }
}