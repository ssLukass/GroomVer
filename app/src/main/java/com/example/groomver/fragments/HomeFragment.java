package com.example.groomver.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.groomver.R;
import com.example.groomver.adapters.ProductsAdapter;
import com.example.groomver.interfaces.ProductClickCallback;
import com.example.groomver.interfaces.ProductListCallback;
import com.example.groomver.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private FirebaseDatabase db;
    private RecyclerView rvProducts;

    private void init(View view){
        db = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/");
        rvProducts = view.findViewById(R.id.rv_products);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Toast.makeText(requireContext(), getString(R.string.Home), Toast.LENGTH_SHORT).show();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        getProductList(new ProductListCallback() {
            @Override
            public void onListReceived(ArrayList<Product> products) {
                ProductsAdapter adapter = new ProductsAdapter(requireContext(), new ProductClickCallback() {
                    @Override
                    public void onClick(Product product) {
                        Intent intent = new Intent();
                        intent.putExtra("PRORUDCT_ID", product.getKey());
                        Toast.makeText(requireActivity(), product.getKey(), Toast.LENGTH_SHORT).show();
                    }
                });

                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                rvProducts.setLayoutManager(gridLayoutManager);
                rvProducts.setAdapter(adapter);
                adapter.setList(products);
            }
        });
    }

    private void getProductList(ProductListCallback callback){
        db.getReference("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Product> products = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    products.add(product);
                }

                callback.onListReceived(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}