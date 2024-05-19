package com.example.groomver.interfaces;

import com.example.groomver.models.Product;

import java.util.ArrayList;

public interface ProductListCallback {
    void onListReceived(ArrayList<Product> products);
}
