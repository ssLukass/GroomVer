package com.example.groomver.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.groomver.R;
import com.example.groomver.fragments.ChatFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.example.groomver.fragments.AddFragment;
import com.example.groomver.fragments.HomeFragment;
import com.example.groomver.fragments.ProfileFragment;
import com.example.groomver.fragments.SearchFragment;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {

        SharedPreferences.Editor editor = getSharedPreferences("UserData", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();


        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;


                if (item.getItemId() == R.id.page_home){
                    fragment = new HomeFragment();
                    bottomNavigationView.setSelectedItemId(R.id.page_home);
                } else if (item.getItemId() == R.id.page_seatch){
                    fragment = new SearchFragment();
                    bottomNavigationView.setSelectedItemId(R.id.page_seatch);
                } else if (item.getItemId() == R.id.page_add) {
                    fragment = new AddFragment();
                    bottomNavigationView.setSelectedItemId(R.id.page_add);
                }else if (item.getItemId() == R.id.page_masseges){
                    bottomNavigationView.setSelectedItemId(R.id.page_masseges);
                    fragment = new ChatFragment();
                }else if(item.getItemId() == R.id.page_profile) {
                    bottomNavigationView.setSelectedItemId(R.id.page_profile);
                    fragment = new ProfileFragment();
                }

                if(fragment != null){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, fragment , null)
                            .commit();

                    drawerLayout.closeDrawers();

                    return true;
                }

                if(item.getItemId() == R.id.about){
                    bottomNavigationView.setSelectedItemId(R.id.about);
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                }else if(item.getItemId() == R.id.sign_out){
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    logoutUser();
                }

                return true;
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, new HomeFragment() , null)
                .commit();

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
                    fragment = new ChatFragment();
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