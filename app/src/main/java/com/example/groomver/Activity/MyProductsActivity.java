package com.example.groomver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groomver.adapters.MyProductsAdapter;
import com.example.groomver.databinding.ActivityMyProductsBinding;
import com.example.groomver.models.Auth;
import com.example.groomver.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyProductsActivity extends AppCompatActivity {
    private ActivityMyProductsBinding binding;
    private FirebaseDatabase database;

    private void init() {
        database = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        database.getReference("products")
                .orderByChild("ownerUID")
                .equalTo(Auth.getCurrentUser().getUID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Product> productsList = new ArrayList<>();
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            Product product = productSnapshot.getValue(Product.class);
                            productsList.add(product);
                        }

                        if (productsList.isEmpty()) {
                            binding.recyclerViewProducts.setVisibility(View.GONE);
                            binding.tvFavorite.setVisibility(View.VISIBLE);
                        } else {
                            binding.recyclerViewProducts.setVisibility(View.VISIBLE);
                            binding.tvFavorite.setVisibility(View.GONE);
                        }

                        MyProductsAdapter adapter = new MyProductsAdapter(productsList, product -> {
                            Intent intent = new Intent(MyProductsActivity.this, DetailsActivity.class);
                            intent.putExtra("PRODUCT_ID", product.getKey());
                            startActivity(intent);
                        }, MyProductsActivity.this);

                        binding.recyclerViewProducts.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
