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
import com.example.groomver.models.Product;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ProductViewHolder> {

    public interface ProductClickCallback {
        void onClick(Product product);
    }

    private List<Product> productList;
    private ProductClickCallback clickCallback;

    public SearchAdapter(List<Product> productList, ProductClickCallback clickCallback) {
        this.productList = productList;
        this.clickCallback = clickCallback;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_search, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvTitle.setText(product.getTitle());
        holder.tvPrice.setText(String.format("%s ₸", product.getPrice())); // Format the price with the currency symbol
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImage())
                    .into(holder.ivProductImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCallback.onClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvPrice;
        ImageView ivProductImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvProductTitle);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }
    }
}
