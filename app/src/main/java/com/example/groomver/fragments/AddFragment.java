package com.example.groomver.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.groomver.R;
import com.example.groomver.interfaces.ImageUploadCallback;
import com.example.groomver.interfaces.OnDataUserReceivedCallback;
import com.example.groomver.models.Auth;
import com.example.groomver.models.Product;
import com.example.groomver.models.User;
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
import java.io.InputStream;
import java.util.Calendar;

public class AddFragment extends Fragment {

    private FirebaseDatabase db;
    private FirebaseStorage storage;

    private FirebaseAuth auth;

    private ImageView ivProduct;
    private EditText etNameProduct;
    private EditText etDescriptionProduct;
    private EditText etPrice;
    private Button bPublish;

    private Bitmap productImage;


    private final ActivityResultLauncher<Intent> openGalleryResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            Uri selectedImageUri = intent.getData();
                            if (selectedImageUri != null) {
                                try {
                                    InputStream is = requireActivity().getContentResolver().openInputStream(selectedImageUri);
                                    productImage = BitmapFactory.decodeStream(is);
                                    ivProduct.setImageBitmap(productImage);
                                } catch (FileNotFoundException exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        ivProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                openGalleryResult.launch(intent);
            }
        });

        bPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void init(View view) {
        ivProduct = view.findViewById(R.id.ivProduct);
        etNameProduct = view.findViewById(R.id.etProductName);
        etDescriptionProduct = view.findViewById(R.id.etDescriptionProduct);
        etPrice = view.findViewById(R.id.etProductPrice);
        bPublish = view.findViewById(R.id.bPublish);
        db = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/");
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private void validateData() {
        String nameProduct = etNameProduct.getText().toString().trim();
        String descriptionProduct = etDescriptionProduct.getText().toString().trim();
        String price = etPrice.getText().toString().trim();

        if (TextUtils.isEmpty(nameProduct)) {
            Toast.makeText(requireContext(), getString(R.string.Name_product), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(descriptionProduct)) {
            Toast.makeText(requireContext(), getString(R.string.Description_product), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(requireContext(), getString(R.string.Price_product), Toast.LENGTH_SHORT).show();
        } else {
            saveAdToDatabase();
        }
    }

    private void saveAdToDatabase() {
        String nameProduct = etNameProduct.getText().toString().trim();
        String descriptionProduct = etDescriptionProduct.getText().toString().trim();
        String price = etPrice.getText().toString().trim();

        Product myProduct = new Product();

        myProduct.setTitle(nameProduct);
        myProduct.setDescription(descriptionProduct);
        myProduct.setPrice(Integer.parseInt(price));

        Calendar calendar = Calendar.getInstance();
        myProduct.setCreationDate(calendar.getTimeInMillis());

        getDatabaseCurrentUser(new OnDataUserReceivedCallback() {
            @Override
            public void onUserReceived(User user) {
                myProduct.setOwnerUID(user.getUID());
                if(productImage != null){
                    uploadImage(productImage, new ImageUploadCallback() {
                        @Override
                        public void onImageUpload(String url) {
                            myProduct.setImage(url);
                            Log.d("USER_UID", url);
                            uploadProduct(myProduct);
                        }
                    });
                }else{
                    uploadProduct(myProduct);
                }
            }
        });
    }

    private void uploadProduct(Product product){
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("products").push();
        String key = myRef.getKey();
        product.setKey(key);

        myRef.setValue(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), getString(R.string.Product_publish), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.Error_product_publish), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void uploadImage(Bitmap bitmap, ImageUploadCallback callback) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        StorageReference profileImages = storage.getReference("ads");
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
                    callback.onImageUpload(task.getResult().toString());
                }
            }
        });
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
}
