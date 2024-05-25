package com.example.groomver.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.groomver.R;
import com.example.groomver.interfaces.OnChatItemClickListener;
import com.example.groomver.models.Chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private ArrayList<Chat> chats;
    private OnChatItemClickListener listener;
    private int ONE_DAY = 86400000;

    public ChatAdapter(ArrayList<Chat> chats, OnChatItemClickListener l) {
        this.chats = chats;
        this.listener = l;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageAvatar;
        TextView userName, text, timestamp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.image_avatar);
            userName = itemView.findViewById(R.id.tv_user_name);
            text = itemView.findViewById(R.id.tv_text);
            timestamp = itemView.findViewById(R.id.tv_timestamp);
        }

        void bind(Chat chat, int position){
            itemView.setOnClickListener(view ->{
                listener.onChatClicked(chat, position);
            });

        }
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat, position);
        if (!TextUtils.isEmpty(chat.getUserAvatar())){
            Glide.with(holder.itemView.getContext())
                    .load(chat.getUserAvatar())
                    .centerCrop()
                    .into(holder.imageAvatar);
        }else{
            holder.imageAvatar.setImageDrawable(holder
                    .itemView
                    .getContext()
                    .getDrawable(R.drawable.profile));
        }
        if (!TextUtils.isEmpty(chat.getUserName())){
            holder.userName.setText(chat.getUserName());
        }else{
            holder.userName.setText(chat.getEmail());
        }
        if (chat.getText().length() > 30){
            holder.text.setText(chat.getText().substring(0, 30) + "...");
        }else{
            holder.text.setText(chat.getText());
        }

        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        long timestamp = chat.getTimestamp();
        SimpleDateFormat format;

        if (currentTime - timestamp > ONE_DAY){
            format = new SimpleDateFormat("dd.MM");
            holder.timestamp.setText(format.format(timestamp));
        }else{
            format = new SimpleDateFormat("HH:mm");
            holder.timestamp.setText("Сегодня" + " " + format.format(timestamp));
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
