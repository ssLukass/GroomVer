package com.example.groomver.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.groomver.R;
import com.example.groomver.models.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.groomver.models.User;

public class RegisterActivity extends AppCompatActivity {


    private Button registerButton;
    private EditText etEmail, etNameInput, etCreatePassword, etRepeatPassword;

    private FirebaseDatabase db;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/");
        auth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.register_button);
        etEmail = findViewById(R.id.etEmail);
        etNameInput = findViewById(R.id.etUsername);
        etCreatePassword = findViewById(R.id.etPassword);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);

        registerButton.setOnClickListener(v -> validateData());
    }

    /**
     * Checks the data entered by the user when registering a new account.
     * If all the data is filled in correctly, calls the registerUser method to register a new user.
     * If the data does not meet the requirements, displays error messages.
     */
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


/*  private void isUserExist(String userName, String userEmail, String createPassword){
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
    }*/

    /**
     * Registers a new user with the specified name, email address and password.
     * After successful registration, calls the CreateAccount method to create a user profile.
     *
     * @param userName is the user's name.
     * @param userEmail The email address of the new user.
     * @param userPassword is the password of the new user.
     */
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

    /**
     * Creates a user account with the specified name, email address, password and user ID (UID).
     * After creating the account, saves the user's data in the Firebase database and redirects the user to the main screen of the application.
     *
     * @param userName is the user's name.
     * @param userEmail The user's email address.
     * @param createPassword is the user's password.
     * @param UID User ID.
     */
    private void createAccount(String userName, String userEmail, String createPassword, String UID) {

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", userEmail);
        editor.putString("password", createPassword);
        editor.apply();

        DatabaseReference users = db.getReference("users").push();

        User user = new User(userName, userEmail, UID);
        String key = users.getKey();
        user.setKey(key);

        users.setValue(user);

        Auth.getDatabaseCurrentUser(us ->{
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}