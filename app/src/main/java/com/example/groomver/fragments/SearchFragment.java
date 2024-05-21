package com.example.groomver.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.groomver.R;
import com.example.groomver.adapters.ProductAdapter;
import com.example.groomver.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvSearchResults;
    private ProductAdapter productAdapter;
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
        productAdapter = new ProductAdapter(productList);
        rvSearchResults.setAdapter(productAdapter);

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
        DatabaseReference productsRef = db.getReference("products");
        productsRef.orderByChild("title").startAt(query).endAt(query + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Product product = ds.getValue(Product.class);
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
    }
}
