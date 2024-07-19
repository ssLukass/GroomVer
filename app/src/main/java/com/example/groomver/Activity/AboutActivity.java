package com.example.groomver.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.groomver.R;

public class AboutActivity extends AppCompatActivity {
    private ImageView telegram, instagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        telegram = findViewById(R.id.image_telegram);
        instagram = findViewById(R.id.image_instagram);

        telegram.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://t.me/ss_LuKa_ss"));
            startActivity(intent);
        });

        instagram.setOnClickListener(v -> {
           Intent intent = new Intent(Intent.ACTION_VIEW);
           intent.setData(Uri.parse("https://www.instagram.com/admiral.ss/"));
           startActivity(intent);
        });
    }
}