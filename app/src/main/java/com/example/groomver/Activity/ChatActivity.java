package com.example.groomver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.groomver.R;
import com.example.groomver.adapters.MessagesAdapter;
import com.example.groomver.databinding.ActivityChatBinding;
import com.example.groomver.interfaces.OnAvatarClickListened;
import com.example.groomver.models.Auth;
import com.example.groomver.models.Message;
import com.example.groomver.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {
    private FirebaseDatabase db;
    private DatabaseReference chats;
    private ActivityChatBinding binding;
    private User companionUser;
    private String roomID;
    private DatabaseReference chat;
    private ValueEventListener listener;
    private ArrayList<Message> messages;
    private MessagesAdapter adapter;

    private void init(){
        db = FirebaseDatabase.getInstance("https://newgroomver-default-rtdb.europe-west1.firebasedatabase.app/");
        chats = db.getReference("Chats");
        messages = new ArrayList<>();

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Message message = ds.getValue(Message.class);
                    messages.add(message);
                }
                adapter.notifyDataSetChanged();
                binding.recyclerViewMessages.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       /* setSupportActionBar(binding.toolbar);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_baseline_arrow_back_ios_24));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        init();

        Intent intent = getIntent();
        String companionKey = intent.getStringExtra("companionKey");
        initCompanionInfo(companionKey);


        /*binding.companionUserImage.setOnClickListener(view -> {
            if (binding.progressBar.getVisibility() == View.GONE){
                Intent showUserProfile = new Intent(ChatActivity.this,
                        ShowProfileActivity.class);
                showUserProfile.putExtra("key", companionUser.getKey());
                startActivity(showUserProfile);
            }
        });*/

        binding.btnSend.setOnClickListener(view ->{
            String text = binding.etInput.getText().toString().trim();
            if (!TextUtils.isEmpty(text)){
                Calendar calendar = Calendar.getInstance();
                Message message = new Message(calendar.getTimeInMillis(),
                        Auth.getCurrentUser().getKey(), text, Auth.getCurrentUser().getKey(), companionKey);
                chats.child(roomID).push().setValue(message);
                binding.etInput.setText("");
            }
        });
    }


    private void initCompanionInfo(String companionKey){
        binding.companionUserImage.setClickable(false);
        showProgressBar();
        Auth.getUserByKey(companionKey, user -> {
            companionUser = user;
            chats.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(companionUser.getKey() + Auth.getCurrentUser().getKey())){
                        roomID = companionUser.getKey() + Auth.getCurrentUser().getKey();
                    }else{
                        roomID = Auth.getCurrentUser().getKey() + companionUser.getKey();
                    }
                    Log.d("TAG", "initCompanionInfo: " + roomID);

                    if (!TextUtils.isEmpty(companionUser.getAvatar())){
                        Glide.with(ChatActivity.this)
                                .load(companionUser.getAvatar())
                                .centerCrop()
                                .into(binding.companionUserImage);
                    }else{
                        binding.companionUserImage.setImageDrawable(getDrawable(R.drawable.profile));
                    }
                    if (!TextUtils.isEmpty(companionUser.getUserName())){
                        binding.tvCompanionUserName.setText(companionUser.getUserName());
                    }else{
                        binding.tvCompanionUserName.setText(companionUser.getUserEmail());
                    }
                    chat = db.getReference("Chats").child(roomID);
                    adapter = new MessagesAdapter(messages, companionUser);
                    LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this);
                    binding.recyclerViewMessages.setLayoutManager(manager);
                    binding.recyclerViewMessages.setAdapter(adapter);
                    chat.addValueEventListener(listener);

                    hideProgressBar();
                    binding.companionUserImage.setClickable(true);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }



    private void showProgressBar(){
        //binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        //binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        chat.removeEventListener(listener);
    }
}
