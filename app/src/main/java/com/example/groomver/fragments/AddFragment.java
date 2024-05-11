package com.example.groomver.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import com.example.groomver.R;
import com.example.groomver.models.Ad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddFragment extends Fragment {

    private FirebaseDatabase db;
    private FirebaseStorage storage;
    private DatabaseReference adsRef;

    private ImageView ivProduct;
    private EditText etNameProduct;
    private EditText etDescriptionProduct;
    private EditText etPrice;
    private Button bPublish;

    private Ad myAd;

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
                                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                                    ivProduct.setImageBitmap(bitmap);
                                    uploadImage(bitmap);
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
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        db = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/");
        storage = FirebaseStorage.getInstance();
        adsRef = db.getReference("ads");
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        ivProduct = view.findViewById(R.id.ivProduct);
        etNameProduct = view.findViewById(R.id.etProductName);
        etDescriptionProduct = view.findViewById(R.id.etDescriptionProduct);
        etPrice = view.findViewById(R.id.etProductPrice);
        bPublish = view.findViewById(R.id.bPublish);

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

    private void validateData() {
        String nameProduct = etNameProduct.getText().toString().trim();
        String descriptionProduct = etDescriptionProduct.getText().toString().trim();
        String price = etPrice.getText().toString().trim();

        if (TextUtils.isEmpty(nameProduct)) {
            Toast.makeText(requireContext(), "Введите название продукта", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(descriptionProduct)) {
            Toast.makeText(requireContext(), "Введите описание продукта", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(requireContext(), "Введите цену продукта", Toast.LENGTH_SHORT).show();
        } else {
            // Данные введены корректно, нет необходимости проверять их еще раз перед сохранением
            saveAdToDatabase();
        }
    }

    private void saveAdToDatabase() {
        String nameProduct = etNameProduct.getText().toString().trim();
        String descriptionProduct = etDescriptionProduct.getText().toString().trim();
        String price = etPrice.getText().toString().trim();

        myAd = new Ad();
        myAd.setNameProduct(nameProduct);
        myAd.setDescriptionProduct(descriptionProduct);
        myAd.setPrice(Integer.parseInt(price));

        // Устанавливаем UID для объявления
        String UID = adsRef.push().getKey();
        myAd.setUID(UID);

        // Сохраняем данные о продукте в базе данных
        adsRef.child(UID).setValue(myAd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Продукт опубликован", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Ошибка при публикации продукта", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference profileImages = storage.getReference().child("product_images/" + System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = profileImages.putBytes(data);

        // После установки ссылки на фото продукта
        profileImages.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> uriTask) {
                if (uriTask.isSuccessful()) {
                    Uri downloadUri = uriTask.getResult();
                    if (downloadUri != null) {
                        String imageUrl = downloadUri.toString();
                        myAd.setFotoProduct(imageUrl);

                        // Сохраняем ссылку на фото продукта в базе данных Firebase
                        adsRef.child("product_images").setValue(imageUrl);

                        // Обновляем UI в основном потоке
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Выполнить любые операции с UI, если это необходимо
                                // Например, показать сообщение или обновить элементы интерфейса
                            }
                        });
                    }
                } else {
                    Log.e("my_error", uriTask.getException().getMessage());
                    Toast.makeText(requireContext(), "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}