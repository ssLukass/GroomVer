package com.example.groomver.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groomver.Activity.DetailsActivity;
import com.example.groomver.R;
import com.example.groomver.adapters.SearchAdapter;
import com.example.groomver.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private EditText etSearch;
    private RecyclerView rvSearchResults;
    private SearchAdapter searchAdapter;
    private List<Product> productList;

    private FirebaseDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etSearch);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        searchAdapter = new SearchAdapter(productList, new SearchAdapter.ProductClickCallback() {
            @Override
            public void onClick(Product product) {
                Intent intent = new Intent(requireActivity(), DetailsActivity.class);
                intent.putExtra("PRODUCT_ID", product.getKey());
                startActivity(intent);
            }
        });
        rvSearchResults.setAdapter(searchAdapter);

        db = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/");

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchProducts(String query) {
        Log.d(TAG, "Searching for products with query: " + query);
        final String lowerCaseQuery = query.toLowerCase();
        DatabaseReference productsRef = db.getReference("products");
        productsRef.orderByChild("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    if (product != null && product.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                        productList.add(product);
                        Log.d(TAG, "Product found: " + product.getTitle());
                    } else {
                        Log.d(TAG, "Product does not match query: " + product.getTitle());
                    }
                }
                searchAdapter.notifyDataSetChanged();
                Log.d(TAG, "Number of products found: " + productList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }
}
