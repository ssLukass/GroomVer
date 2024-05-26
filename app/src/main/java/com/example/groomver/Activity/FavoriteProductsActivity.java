package com.example.groomver.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groomver.R;
import com.example.groomver.adapters.ProductsAdapter;
import com.example.groomver.databinding.ActivityFavoriteAdsBinding;
import com.example.groomver.interfaces.OnDataAdsReceivedListener;
import com.example.groomver.interfaces.OnFavoriteClickCallback;
import com.example.groomver.interfaces.ProductClickCallback;
import com.example.groomver.models.Auth;
import com.example.groomver.models.Product;
import com.example.groomver.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoriteProductsActivity extends AppCompatActivity {
    private ActivityFavoriteAdsBinding binding;
    private FirebaseDatabase db;

    private ProductsAdapter adapter;

    private void init(){
        db = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteAdsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        getFavoritesAd( list -> {
            adapter = new ProductsAdapter(
                    FavoriteProductsActivity.this,
                    new ProductClickCallback() {
                        @Override
                        public void onClick(Product product) {
                            Intent intent = new Intent(FavoriteProductsActivity.this,
                                    DetailsActivity.class);
                            intent.putExtra("PRODUCT_ID", product.getKey());
                            startActivity(intent);
                        }
                    },
                    new OnFavoriteClickCallback() {
                        @Override
                        public void onFavoriteClick(Product product) {
                            User newUser = Auth.getCurrentUser();
                            if(product.isFavorite()){
                                newUser.addToFavorites(product);
                            }else{
                                newUser.removeFromFavorites(product);
                            }
                            Auth.updateUserInFireBase(newUser);
                        }
                    }
            );
            adapter.setList(list);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
            binding.recyclerViewProducts.setLayoutManager(manager);
            binding.recyclerViewProducts.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });


    }


    public void getFavoritesAd(OnDataAdsReceivedListener listener){
        DatabaseReference tRef = db.getReference("products");

        ArrayList<String> favoritesKeys = Auth.getCurrentUser().getFavoritesAds();
        ArrayList<Product> resultArr = new ArrayList<>();

        for (String str: favoritesKeys) {
            tRef.orderByChild("key").equalTo(str).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Product ad = ds.getValue(Product.class);
                        ad.setFavorite(true);
                        resultArr.add(ad);
                        ArrayList<Product> sortedResult = new ArrayList<>();
                        for (int i = 0; i < favoritesKeys.size(); i++) {
                            for (int j = 0; j < resultArr.size(); j++) {
                                if (favoritesKeys.get(i).equals(resultArr.get(j).getKey())){
                                    sortedResult.add(resultArr.get(j));
                                    break;
                                }
                            }
                        }
                        listener.onAdsReceived(sortedResult);
                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}