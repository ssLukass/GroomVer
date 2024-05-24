package com.example.groomver.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.groomver.R;
import com.example.groomver.interfaces.ProductClickCallback;
import com.example.groomver.models.Product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private static final String PREFS_NAME = "favorites";
    private static final String FAVORITES_KEY = "product_favorites";

    private ProductClickCallback callback;
    private ArrayList<Product> products = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    public ProductsAdapter(Context context, ProductClickCallback callback) {
        this.callback = callback;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        TextView price;
        ImageView favorite;

        ProductViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_product_title);
            image = itemView.findViewById(R.id.iv_product);
            price = itemView.findViewById(R.id.tv_product_price);
            favorite = itemView.findViewById(R.id.iv_favorite);
        }
    }

    public void setList(ArrayList<Product> products) {
        this.products = products;
        for (Product product : products) {
            product.setFavorite(isFavorite(product.getKey()));
        }
        notifyDataSetChanged();
    }

    private boolean isFavorite(String productId) {
        Set<String> favorites = sharedPreferences.getStringSet(FAVORITES_KEY, new HashSet<>());
        return favorites.contains(productId);
    }

    private void setFavorite(String productId, boolean isFavorite) {
        Set<String> favorites = sharedPreferences.getStringSet(FAVORITES_KEY, new HashSet<>());
        if (isFavorite) {
            favorites.add(productId);
        } else {
            favorites.remove(productId);
        }
        sharedPreferences.edit().putStringSet(FAVORITES_KEY, favorites).apply();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.product_item,
                parent,
                false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.title.setText(product.getTitle());
        holder.price.setText(product.getPrice() + " ₸");
        Glide.with(holder.itemView.getContext())
                .load(product.getImage())
                .into(holder.image);

        holder.favorite.setSelected(product.isFavorite());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(product);
            }
        });

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = !holder.favorite.isSelected();
                holder.favorite.setSelected(isSelected);
                product.setFavorite(isSelected);
                setFavorite(product.getKey(), isSelected);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}