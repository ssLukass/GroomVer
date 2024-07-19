package com.example.groomver.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsAdapter.ProductViewHolder> {
    private static final String TAG = "MyProductsAdapter";
    private ArrayList<Product> list;
    private ProductClickCallback callback;
    private Context context;

    public MyProductsAdapter(ArrayList<Product> list, ProductClickCallback callback, Context context) {
        this.list = list;
        this.callback = callback;
        this.context = context;
    }

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

        holder.itemView.setOnClickListener(v -> callback.onClick(product));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.Delete_Ad)
                        .setMessage(R.string.Are_You_Sure_You_Want_To_Delete_This_Ad)
                        .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String productKey = product.getKey();
                                FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/").getReference("products")
                                        .child(productKey)
                                        .removeValue();
                                list.remove(product);
                                notifyDataSetChanged();
                                Log.d("Delete4234", productKey);
                            }
                        })
                        .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
