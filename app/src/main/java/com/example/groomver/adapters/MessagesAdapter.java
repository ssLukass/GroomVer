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
import com.example.groomver.interfaces.OnAvatarClickListened;
import com.example.groomver.models.Auth;
import com.example.groomver.models.Message;
import com.example.groomver.models.User;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private int COMPANION_MESSAGE = 0;
    private int MY_MESSAGE = 1;
    private User user;
    private User companionUser;
    private ArrayList<Message> messages;

    public MessagesAdapter(ArrayList<Message> messages, User companionUser) {
        this.messages = messages;
        this.companionUser = companionUser;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textViewMessage);
        }
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == COMPANION_MESSAGE){
            v = inflater.inflate(R.layout.item_message_received, parent, false);
        }else{
            v = inflater.inflate(R.layout.item_message_sent, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.text.setText(message.getText());
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getOwnerKey().equals(Auth.getCurrentUser().getKey())) {
            user = Auth.getCurrentUser();
            //image = imageCurrentUser;
            return MY_MESSAGE;
        }else {
            user = companionUser;
            //image = imageCompanionUser;
            return COMPANION_MESSAGE;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}