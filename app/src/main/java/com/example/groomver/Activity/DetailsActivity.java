package com.example.groomver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.groomver.R;
import com.example.groomver.interfaces.OnDataUserReceivedCallback;
import com.example.groomver.interfaces.OnProductReceivedCallback;
import com.example.groomver.models.Product;
import com.example.groomver.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference products;
    private DatabaseReference users;

    private ImageView ivProductImage;
    private TextView tvProductTitle;
    private TextView tvProductPrice;
    private TextView tvProductCity;
    private TextView tvProductDescription;
    private ImageView ivUserAvatar;
    private TextView tvUserName;
    private TextView tvCreationDate;
    private Button btnWrite;

    private void initViews(){
        ivProductImage = findViewById(R.id.iv_product_image);
        tvProductTitle = findViewById(R.id.tv_product_title);
        tvProductPrice = findViewById(R.id.tv_product_price);
        tvProductCity = findViewById(R.id.tv_product_city);
        tvProductDescription = findViewById(R.id.tv_product_description);
        ivUserAvatar = findViewById(R.id.iv_user_avatar);
        tvUserName = findViewById(R.id.tv_user_name);
        tvCreationDate = findViewById(R.id.tv_creation_date);
        btnWrite = findViewById(R.id.btn_write);
    }

    private void init(){
        database = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/");
        products = database.getReference("products");
        users = database.getReference("users");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        init();

        Intent intent = getIntent();
        String productKey = intent.getStringExtra("PRODUCT_ID");
        Log.d("ProductKey", productKey);

        getProductByKey(productKey, product -> {
            if (product != null) {
                Glide.with(this)
                        .load(product.getImage())
                        .into(ivProductImage);
                tvProductTitle.setText(product.getTitle());
                tvProductPrice.setText(String.format("₸%d", product.getPrice()));
                tvProductCity.setText(product.getCity());
                tvProductDescription.setText(product.getDescription());

                long creationTimeMillis = product.getCreationDate();
                Log.d("ASDLKADKLASKDL", creationTimeMillis + "");

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String creationDate = sdf.format(new Date(creationTimeMillis));
                tvCreationDate.setText(getString(R.string.Data) + creationDate);

                getUserByOwnerUid(product.getOwnerUID(), user -> {
                    if (user != null) {
                        Glide.with(this)
                                .load(user.getAvatar())
                                .into(ivUserAvatar);
                        tvUserName.setText(user.getUserName());

                        btnWrite.setOnClickListener( view ->{
                            Intent newIntent = new Intent(DetailsActivity.this, ChatActivity.class);
                            newIntent.putExtra("companionKey", user.getKey());
                            startActivity(newIntent);
                        });
                    }
                });
            }
        });
    }

    public void getUserByOwnerUid(String uid, OnDataUserReceivedCallback listener) {
        users.orderByChild("uid").equalTo(uid).limitToFirst(1)
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

    private void getProductByKey(String key, OnProductReceivedCallback callback){
        products.orderByChild("key").equalTo(key).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot productSnapshot : snapshot.getChildren()){
                            Product product = productSnapshot.getValue(Product.class);
                            callback.onProductReceived(product);
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}