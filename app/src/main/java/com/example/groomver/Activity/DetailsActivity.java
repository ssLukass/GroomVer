package com.example.groomver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class DetailsActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference products;
    private DatabaseReference users;

    private Button btnWrite;

    private void initViews(){
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
        Log.d("KLASDLKASD", productKey);

        getProductByKey(productKey, product -> {
            // Заполнить все View

            getUserByOwnerUid(product.getOwnerUID(), user -> {
                btnWrite.setOnClickListener(view ->{
                    Intent newIntent = new Intent(DetailsActivity.this, ChatActivity.class);
                    newIntent.putExtra("companionKey", user.getKey());
                    startActivity(newIntent);
                });
            });
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

