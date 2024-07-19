package com.example.groomver.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.groomver.Activity.DetailsActivity;
import com.example.groomver.R;
import com.example.groomver.adapters.ProductsAdapter;
import com.example.groomver.interfaces.OnFavoriteClickCallback;
import com.example.groomver.interfaces.ProductClickCallback;
import com.example.groomver.interfaces.ProductListCallback;
import com.example.groomver.models.Auth;
import com.example.groomver.models.Product;
import com.example.groomver.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {
    private FirebaseDatabase db;
    private RecyclerView rvProducts;
    private ValueEventListener listener;

    private DatabaseReference products;

    private void init(View view) {
        db = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/");
        rvProducts = view.findViewById(R.id.rv_products);
        products = db.getReference("products");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        getProductList(new ProductListCallback() {
            @Override
            public void onListReceived(ArrayList<Product> products) {
                for (Product product : products) {
                    if (Auth.getCurrentUser().getFavoritesAds().contains(product.getKey())) {
                        product.setFavorite(true);
                    }
                }
                ProductsAdapter adapter = new ProductsAdapter(requireContext(), new ProductClickCallback() {
                    @Override
                    public void onClick(Product product) {
                        Intent intent = new Intent(requireActivity(), DetailsActivity.class);
                        intent.putExtra("PRODUCT_ID", product.getKey());
                        startActivity(intent);
                    }
                }, new OnFavoriteClickCallback() {
                    @Override
                    public void onFavoriteClick(Product product) {
                        User newUser = Auth.getCurrentUser();
                        if (product.isFavorite()) {
                            newUser.addToFavorites(product);
                        } else {
                            newUser.removeFromFavorites(product);
                        }
                        Auth.updateUserInFireBase(newUser);
                    }
                });

                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                rvProducts.setLayoutManager(gridLayoutManager);
                rvProducts.setAdapter(adapter);
                adapter.setList(products);
            }
        });
    }

    private void getProductList(ProductListCallback callback) {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Product> products = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    products.add(product);
                }
                Collections.reverse(products);
                callback.onListReceived(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        products.addValueEventListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        products.removeEventListener(listener);
    }
}
