package com.example.groomver.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.groomver.interfaces.OnDataUserReceivedCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Auth{
    public static FirebaseDatabase db = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/");
    public static DatabaseReference users = db.getReference("users");
    public static User currentUser;
    public static FirebaseAuth auth = FirebaseAuth.getInstance();

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void getDatabaseCurrentUser(OnDataUserReceivedCallback listener) {
        FirebaseUser userFBAuth = auth.getCurrentUser();

        Log.d("userFBAuth", userFBAuth.getUid());
        if (userFBAuth != null ) {
            users.orderByChild("uid").equalTo(userFBAuth.getUid()).limitToFirst(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                User us = ds.getValue(User.class);
                                Log.d("currentUser", "currentUser: " + us.getKey());
                                listener.onUserReceived(us);
                                currentUser = us;
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    public static FirebaseUser getCurrentUserFBAuth() {
        return auth.getCurrentUser();
    }

    public static void getUserByKey(String key, OnDataUserReceivedCallback listener) {
        users.orderByChild("key").equalTo(key).limitToFirst(1)
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




    public static void updateUserInFireBase(User user){
        users.child(user.getKey()).setValue(user);
        currentUser = user;
    }
}
