package com.example.groomver.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.groomver.R;
import com.example.groomver.interfaces.OnFavoriteClickCallback;
import com.example.groomver.interfaces.ProductClickCallback;
import com.example.groomver.models.Product;

import java.util.ArrayList;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsAdapter.ProductViewHolder> {
    private ArrayList<Product> list = new ArrayList();
    private ProductClickCallback callback;


    public void setList(ArrayList<Product> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image, delete;
        TextView price;

        ProductViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvProductTitle);
            image = itemView.findViewById(R.id.ivProductImage);
            price = itemView.findViewById(R.id.tvProductPrice);
            delete = itemView.findViewById(R.id.ivDeleteAd);
        }
    }

    public MyProductsAdapter(ArrayList<Product> list, ProductClickCallback callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_ads, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = list.get(position);

        holder.title.setText(product.getTitle());
        holder.price.setText(product.getPrice() + " ₸");
        Glide.with(holder.itemView.getContext())
                .load(product.getImage())
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(product);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
