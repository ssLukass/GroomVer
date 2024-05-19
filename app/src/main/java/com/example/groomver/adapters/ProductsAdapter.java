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
import com.example.groomver.interfaces.ImageUploadCallback;
import com.example.groomver.interfaces.ProductClickCallback;
import com.example.groomver.models.Product;

import java.util.ArrayList;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>{

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView image;
        TextView price;

        ProductViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.tv_product_title);
            image = itemView.findViewById(R.id.iv_product);
            price = itemView.findViewById(R.id.tv_product_price);
        }
    }

    private ProductClickCallback callback;

    public ProductsAdapter(ProductClickCallback callback){
        this.callback = callback;
    }

    private ArrayList<Product> products = new ArrayList<>();


    public void setList(ArrayList<Product> products){
        this.products = products;
        notifyDataSetChanged();
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
        holder.price.setText(product.getPrice() + " тенге");
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
        return products.size();
    }


}
