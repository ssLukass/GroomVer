package com.example.groomver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import fragments.AddFragment;
import fragments.HomeFragment;
import fragments.MessegeFragment;
import fragments.ProfileFragment;
import fragments.SearchFragment;


public class MainActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                if (item.getItemId() == R.id.page_home){
                    fragment = new HomeFragment();
                } else if (item.getItemId() == R.id.page_seatch){
                    fragment = new SearchFragment();
                } else if (item.getItemId() == R.id.page_add) {
                    fragment = new AddFragment();
                }else if (item.getItemId() == R.id.page_masseges){
                    fragment = new MessegeFragment();
                }else {
                    fragment = new ProfileFragment();
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, fragment , null)
                        .commit();

                return true;
            }
        });
    }
}