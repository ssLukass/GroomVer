package com.example.groomver.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.groomver.Activity.FavoriteProductsActivity;
import com.example.groomver.Activity.LoginActivity;
import com.example.groomver.Activity.MyProductsActivity;
import com.example.groomver.R;
import com.example.groomver.interfaces.OnDataUserReceivedCallback;
import com.example.groomver.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ProfileFragment extends Fragment {

    private FirebaseStorage storage;
    private ImageView ivAvatar;
    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private TextView userName;
    private TextView userEmail, tvMyProducts, tvFavorites;
    private User myUser;

    private final ActivityResultLauncher<Intent> openGalleryResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        Uri imageUri = intent.getData();
                        try {
                            // Загрузка изображения с помощью Glide
                            Glide.with(ProfileFragment.this)
                                    .asBitmap()
                                    .load(imageUri)
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            ivAvatar.setImageBitmap(resource);
                                            uploadImage(resource);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {

                                        }
                                    });
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        tvMyProducts.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyProductsActivity.class);
            startActivity(intent);
        });

        tvFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FavoriteProductsActivity.class);
            startActivity(intent);
        });

        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            openGalleryResult.launch(intent);
        });

        Button buttonExit = view.findViewById(R.id.button_exit);
        buttonExit.setOnClickListener(v -> logoutUser());


    }





    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        StorageReference profileImages = storage.getReference("avatars");
        StorageReference currentImage = profileImages.child(System.currentTimeMillis() + "");
        UploadTask uploadTask = currentImage.putBytes(bytes);

        uploadTask.continueWithTask(task -> currentImage.getDownloadUrl())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        myUser.setAvatar(task.getResult().toString());
                        updateUserInFireBase(myUser);
                    }
                });
    }

    private void logoutUser() {

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("UserData", getContext().MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();


        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void updateUserInFireBase(User user) {
        DatabaseReference users = db.getReference("users");
        users.child(user.getKey()).setValue(user);
    }

    public void getDatabaseCurrentUser(OnDataUserReceivedCallback listener) {
        DatabaseReference users = db.getReference("users");
        FirebaseUser userFBAuth = auth.getCurrentUser();

        if (userFBAuth != null) {
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

    private void init(View view) {
        storage = FirebaseStorage.getInstance();
        ivAvatar = view.findViewById(R.id.imageview_profile);
        db = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app//");
        auth = FirebaseAuth.getInstance();
        userName = view.findViewById(R.id.textview_userName);
        userEmail = view.findViewById(R.id.textview_userEmail);
        tvMyProducts = view.findViewById(R.id.textview_ads);
        tvFavorites = view.findViewById(R.id.textview_favorites);

        getDatabaseCurrentUser(new OnDataUserReceivedCallback() {
            @Override
            public void onUserReceived(User user) {
                if (user != null) {
                    myUser = user;
                    userName.setText(user.getUserName());
                    userEmail.setText(user.getUserEmail());
                    if (user.getAvatar() != null) {
                        Glide.with(ProfileFragment.this)
                                .load(user.getAvatar())
                                .centerCrop()
                                .into(ivAvatar);
                    }
                }
            }
        });
    }
}
