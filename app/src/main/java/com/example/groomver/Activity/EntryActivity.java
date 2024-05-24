package com.example.groomver.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.groomver.R;
import com.example.groomver.databinding.ActivityEntryBinding;

public class EntryActivity extends AppCompatActivity {
    private ActivityEntryBinding binding;
    private Animation anim;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        anim = AnimationUtils.loadAnimation(this, R.anim.tv_anim);
        binding.tvWelcome.startAnimation(anim);
        binding.imageIconApp.startAnimation(anim);
        binding.tvCopyright.startAnimation(anim);
        sp = getSharedPreferences("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/", MODE_PRIVATE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String UID = sp.getString("UID", null);
                String email = sp.getString("email", null);
                String passwd = sp.getString("passwd",  null);

                if (UID == null || email == null || passwd == null){
                    Intent intent = new Intent(EntryActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }else{
                    Auth.signIn(EntryActivity.this, email, passwd, new OnUserSignedListener() {
                        @Override
                        public void onUserSigned(boolean isSigned) {
                            if (isSigned){
                                Intent intent = new Intent(EntryActivity.this, FragmentContainerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        }, 2500);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
