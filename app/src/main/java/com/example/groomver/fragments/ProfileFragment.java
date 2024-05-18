package com.example.groomver.fragments;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.groomver.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.example.groomver.models.User;

public class ProfileFragment extends Fragment {

    private FirebaseStorage storage;
    private ImageView ivAvatar;
    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private TextView userName;
    private TextView userEmail;
    private User myUser;

    private final ActivityResultLauncher<Intent> openGalleryResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        try {
                            Uri imageUri = intent.getData();
                            InputStream is = requireActivity().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);

                            // Check and adjust orientation
                            bitmap = adjustImageOrientation(imageUri, bitmap);

                            ivAvatar.setImageBitmap(bitmap);
                            uploadImage(bitmap);
                        } catch (FileNotFoundException exception) {
                            exception.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
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
        Toast.makeText(getContext(), getString(R.string.Profile), LENGTH_SHORT).show();

        init(view);

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                openGalleryResult.launch(intent);
            }
        });

    }

    private Bitmap adjustImageOrientation(Uri imageUri, Bitmap bitmap) throws IOException {
        InputStream input = requireActivity().getContentResolver().openInputStream(imageUri);
        ExifInterface exif = new ExifInterface(input);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        StorageReference profileImages = storage.getReference("avatars");
        StorageReference currentImage = profileImages.child(System.currentTimeMillis() + "");
        UploadTask uploadTask = currentImage.putBytes(bytes);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return currentImage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    myUser.setAvatar(task.getResult().toString());
                    updateUserInFireBase(myUser);
                }
            }
        });
    }

    public void updateUserInFireBase(User user) {
        DatabaseReference users = db.getReference("users");
        users.child(user.getKey()).setValue(user);
    }

    public void getDatabaseCurrentUser(OnDataUserReceivedListener listener) {
        DatabaseReference users = db.getReference("users");
        FirebaseUser userFBAuth = auth.getCurrentUser();

        if (userFBAuth != null) {
            users.orderByChild("uid").equalTo(userFBAuth.getUid()).limitToFirst(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                User user = ds.getValue(User.class);
                                listener.onUserReceived(user); // Pass the user object to the listener
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled
                        }
                    });
        }
    }

    interface OnDataUserReceivedListener {
        void onUserReceived(User user);
    }

    private void init(View view) {
        storage = FirebaseStorage.getInstance();
        ivAvatar = view.findViewById(R.id.imageview_profile);
        db = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app//");
        auth = FirebaseAuth.getInstance();
        userName = view.findViewById(R.id.textview_userName);
        userEmail = view.findViewById(R.id.textview_userEmail);

        getDatabaseCurrentUser(new OnDataUserReceivedListener() {
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
